package io.github.zap.regularcommands.commands;

import io.github.zap.regularcommands.completer.ArgumentCompleter;
import io.github.zap.regularcommands.converter.ArgumentConverter;
import io.github.zap.regularcommands.converter.ConversionResult;
import io.github.zap.regularcommands.converter.MatchResult;
import io.github.zap.regularcommands.converter.Parameter;
import io.github.zap.regularcommands.util.ArrayUtils;
import io.github.zap.regularcommands.util.Completers;
import io.github.zap.regularcommands.util.StringUtils;
import io.github.zap.regularcommands.validator.CommandValidator;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * Defines a specific form of a command. Subclasses define the action taken by the command when it is executed, as well
 * as determine if it should be executed based off of various conditions. CommandForm objects are immutable. They also
 * implement the Iterable interface, which can be used to loop through their defining {@link Parameter} instances.
 */
public abstract class CommandForm<T> implements Iterable<Parameter> {
    private final class ParameterIterator implements Iterator<Parameter> {
        private int index = -1;

        @Override
        public boolean hasNext() {
            return index + 1 < parameters.length;
        }

        @Override
        public Parameter next() {
            return parameters[++index];
        }
    }

    private final RegularCommand command;
    private final Component usage;
    private final Parameter[] parameters;
    private final PermissionData permissions;
    private final int requiredLength;

    private final boolean vararg;
    private final boolean optional;

    /**
     * Creates a CommandForm.
     *
     * Each CommandForm can be said to have a 'signature' that is defined by the provided Parameter array. Two
     * different CommandForms can have 'signature overlap' where both can be executed given one set of inputs.
     * In this case, both forms will be executed. The order in which they are run depends on the order that they were
     * added to their RegularCommand instance - command forms added later will always be executed after forms added
     * earlier.
     *
     * The Parameter array is also validated to ensure some basic assumptions can be made about the signature. 'vararg'
     * parameters cannot appear before non-vararg parameters, optional and vararg parameters cannot be mixed, and finally
     * optional parameters themselves cannot appear before 'regular' parameters.
     * @param command The RegularCommand instance this CommandForm is tied to
     * @param usage A short, user-friendly description of what the command does
     * @param permissionData The permissions required to execute this command
     * @param parameters The parameters array that defines the signature of this command
     */
    public CommandForm(@NotNull RegularCommand command, @NotNull Component usage, @NotNull PermissionData permissionData,
                       @NotNull Parameter... parameters) {
        this.command = Objects.requireNonNull(command, "command cannot be null");
        this.usage = Objects.requireNonNull(usage, "usage cannot be null");
        this.permissions = Objects.requireNonNull(permissionData, "metadata cannot be null");
        this.parameters = Objects.requireNonNull(parameters, "parameters cannot be null");

        boolean optional = false;
        boolean vararg = false;
        int reqLen = 0;

        for (Parameter value : parameters) { //validate parameter array
            Objects.requireNonNull(value, "parameter cannot be null");

            if (vararg) { //can't have varargs before non-varargs
                throw new IllegalArgumentException("varargs parameter must be the last parameter");
            }

            switch (value.getType()) {
                case OPTIONAL:
                    optional = true;
                    break;
                case VARARG:
                    if (optional) { //combining optional and varargs creates ambiguity problems
                        throw new IllegalArgumentException("you cannot mix optional and varargs parameters");
                    }

                    vararg = true;
                    break;
                case STANDARD:
                default:
                    reqLen++;
                    break;
            }

            if (optional && value.getType() != Parameter.ParameterType.OPTIONAL) { //avoids more ambiguity problems
                throw new IllegalArgumentException("non-optional parameters cannot appear after optional parameters");
            }
        }

        requiredLength = reqLen; //length of all non-optional, non-vararg parameters
        this.vararg = vararg;
        this.optional = optional;
    }

    /**
     * Returns a copy of the parameters array.
     * @return A copy of the parameters array
     */
    public @NotNull Parameter[] getParameters() {
        return Arrays.copyOf(parameters, parameters.length);
    }

    /**
     * Gets the parameter at the specified index. Throws an IndexOutOfBounds exception if the parameter index is out of
     * bounds.
     * @param parameterIndex The index of the desired parameter
     * @return The parameter located at the given index
     */
    public @NotNull Parameter getParameter(int parameterIndex) {
        return parameters[parameterIndex];
    }

    /**
     * Returns an iterator for this CommandForm's parameters.
     * @return An iterator that iterates over this CommandForm's parameters
     */
    @Override
    public @NotNull Iterator<Parameter> iterator() {
        return new ParameterIterator();
    }

    /**
     * Returns true if the CommandForm contains at least one optional parameter, false otherwise.
     * @return true if the CommandForm contains at least one optional parameter, false otherwise
     */
    public boolean isOptional() { return optional; }

    /**
     * Returns true if the CommandForm contains a vararg parameter, false otherwise.
     * @return true if the CommandForm contains a vararg parameter, false otherwise
     */
    public boolean isVararg() { return vararg; }

    /**
     * Attempts to match the provided argument array with this CommandForm.
     * @param args The complete input argument array
     * @return A MatchResult argument containing information about the match attempt
     */
    public @NotNull MatchResult matches(String[] args) {
        if(args.length == 0) { //optimization for zero-length parameters
            boolean matches = parameters.length == 0;
            return new MatchResult(this, true, matches, matches ? ConversionResult.of(true,
                    ArrayUtils.EMPTY_OBJECT_ARRAY, null) : null);
        }

        //optimization, don't bother testing if we are above or below the required length for this form
        if(args.length < requiredLength || args.length > parameters.length && !vararg) {
            return new MatchResult(this, true, false, null);
        }

        int iters = Math.max(args.length, parameters.length);
        Object[] result = new Object[iters];

        for(int i = 0; i < iters; i++)
        {
            Parameter parameter = parameters[Math.min(i, parameters.length - 1)];
            Parameter.ParameterType parameterType = parameter.getType();
            String input;

            if(i >= args.length) {
                if(parameterType == Parameter.ParameterType.OPTIONAL) {
                    input = parameter.getDefaultValue(); //parameter is optional and argument is not supplied
                }
                else {
                    input = StringUtils.EMPTY; //parameter must be vararg but user didn't supply any arguments
                }
            }
            else {
                input = args[i]; //take user argument when possible
            }

            if(matchFails(input, parameter)) {
                return new MatchResult(this, true, false, null);
            }

            ArgumentConverter<Object> converter = parameter.getConverter();
            ConversionResult<Object> conversionResult;

            if(converter == null) {
                conversionResult = ConversionResult.of(true, input, null);
            }
            else {
                conversionResult = parameter.getConverter().convert(this, input);
            }

            if(conversionResult.isValid()) { //successful conversion
                result[i] = conversionResult.getConversion();
            }
            else { //failed conversion
                return new MatchResult(this, true, true, ConversionResult.of(false,
                        null, conversionResult.getErrorMessage()));
            }
        }

        return new MatchResult(this, true,true, ConversionResult.of(true, result, null));
    }

    /**
     * Calculates a match score, which describes how close a given set of arguments is to matching this form. It will
     * return -1 if there is no chance that the provided arguments can be made to perfectly match this form by
     * <i>adding</i> additional characters. The last argument is treated specially; if it does not match, it does not
     * cause the argument to return -1
     * @param args The arguments, which may be incomplete.
     * @return The match score for the provided arguments
     */
    public int matchScore(@NotNull String[] args) {
        if(parameters.length == 0 || !vararg && args.length > requiredLength) {
            return -1;
        }

        int i;
        for(i = 0; i < args.length; i++) {
            Parameter parameter = parameters[Math.min(i, parameters.length - 1)];
            String arg = args[i];

            if(matchFails(arg, parameter)) {
                if(i < args.length - 1) {
                    return -1;
                }
                else {
                    return i;
                }
            }
        }

        return i;
    }

    private boolean matchFails(String argument, Parameter parameter) {
        if(parameter.getType() == Parameter.ParameterType.SIMPLE) {
            return !parameter.getMatch().equals(argument);
        }
        else {
            return !parameter.getPattern().matcher(argument).matches();
        }
    }

    /**
     * Returns the RegularCommand instance this form is registered under
     * @return The parent RegularCommand
     */
    public @NotNull RegularCommand getCommand() {
        return command;
    }

    /**
     * Returns a short, user-friendly string that should explain what the form does.
     * @return A user-friendly string explaining what the CommandForm does
     */
    public @NotNull Component getUsage() {
        return usage;
    }

    /**
     * Returns the PermissionData, which is used to determine if the form can be executed based on the sender.
     * @return The PermissionData object
     */
    public PermissionData getPermissions() {
        return permissions;
    }

    /**
     * Gets the argument completer object that is used to tab complete an argument array that partially matches this
     * form.
     * @return The tab completer to use. If not overridden, defaults to Completers.PARAMETER_COMPLETER
     */
    public ArgumentCompleter getCompleter() {
        return Completers.PARAMETER_COMPLETER;
    }

    /**
     * Gets the length of the parameters array.
     * @return The length of the parameters array
     */
    public int size() {
        return parameters.length;
    }

    /**
     * Gets the validator used to perform additional verification on the command parameters, based off of the context
     * or the state of any user-defined objects. This step will always be performed AFTER argument conversion; thus,
     * arguments will contain converted values.
     * @param context The current context
     * @param arguments An array of converted values, whose types correspond to the output of any converters defined
     *                  within the parameters array
     * @return The validator used to determine if the command should execute
     */
    public abstract @Nullable CommandValidator<T, ?> getValidator(Context context, Object[] arguments);

    /**
     * Runs the command after the conversion and validation steps have been performed. The Object[] array passed to
     * this method will always be the same as those passed to getValidator.
     * @param context The current context
     * @param arguments An array of converted values, whose types correspond to any converters defined within the
     *                  parameters array
     * @param data A data object, which if non-null was generated by this form's validator
     * @return A message that will be displayed to the player, and formatted if getStylizer() doesn't return null
     */
    public abstract @Nullable Component execute(Context context, Object[] arguments, T data);
}