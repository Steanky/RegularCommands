package io.github.regularcommands.commands;

import io.github.regularcommands.completer.ArgumentCompleter;
import io.github.regularcommands.converter.MatchResult;
import io.github.regularcommands.converter.Parameter;
import io.github.regularcommands.util.Completers;
import io.github.regularcommands.validator.CommandValidator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * Defines a specific form of a command. Subclasses can define the action taken by the command when it is executed,
 * as well as the determine whether or not it should be executed based off of various conditions. CommandForm objects
 * are immutable.
 */
public abstract class CommandForm implements Iterable<Parameter> {
    private final class ParameterIterator implements Iterator<Parameter> {
        private int index = -1;

        @Override
        public boolean hasNext() {
            return ArrayUtils.isArrayIndexValid(parameters, ++index);
        }

        @Override
        public Parameter next() {
            return parameters[index];
        }
    }


    private final String usage;
    private final Parameter[] parameters;
    private final PermissionData permissions;
    private final int requiredLength;

    private final boolean vararg;
    private final boolean optional;

    /**
     * Creates a CommandForm. In general, each CommandForm object should perform one task or several closely related
     * ones. Each CommandForm can be said to have a 'signature' that is defined by the provided Parameter array. Two
     * different CommandForms can have 'signature overlap' where both can be executed given one set of inputs. In this case,
     * both forms will be executed. The order in which they are run depends on the order that they were added - command
     * forms added later will always be executed after forms added earlier.
     *
     * The Parameter array is also validated to ensure some basic assumptions can be made about the signature. 'vararg'
     * parameters cannot appear before non-vararg parameters, optional and vararg parameters cannot be mixed, and finally
     * optional parameters themselves cannot appear before 'regular' parameters.
     * @param usage A short, user-friendly description of what the command does
     * @param permissionData The permissions required to execute this command
     * @param parameters The parameters array that defines the signature of this command
     */
    public CommandForm(String usage, PermissionData permissionData, Parameter... parameters) {
        this.usage = Objects.requireNonNull(usage, "usage cannot be null");
        this.permissions = Objects.requireNonNull(permissionData, "metadata cannot be null");
        this.parameters = Objects.requireNonNull(parameters, "parameters cannot be null");

        boolean optional = false;
        boolean vararg = false;
        int reqLen = 0;

        //validate parameter array
        for (Parameter value : parameters) {
            if (vararg) { //can't have varargs before non-varargs
                throw new IllegalArgumentException("varargs parameter must be the last parameter");
            }

            if (value.isOptional()) {
                optional = true;
            } else if (value.isVararg()) {
                if (optional) { //combining optional and varargs creates ambiguity problems
                    throw new IllegalArgumentException("you cannot mix optional and varargs parameters");
                }

                vararg = true;
            } else {
                reqLen++;
            }

            if (optional && !value.isOptional()) { //avoids more ambiguity problems
                throw new IllegalArgumentException("non-optional parameters cannot appear after optional parameters");
            }
        }

        requiredLength = reqLen; //length of all non-optional, non-vararg parameters
        this.vararg = vararg;
        this.optional = optional;
    }

    /**
     * Gets the length of the internal parameter array.
     * @return The length of the internal parameter array
     */
    public int length() { return parameters.length; }

    /**
     * Returns a copy of the parameters array.
     * @return A copy of the parameters array
     */
    public Parameter[] getParameters() {
        return Arrays.copyOf(parameters, parameters.length);
    }

    /**
     * Gets the parameter at the specified index. Throws an IndexOutOfBounds exception if the parameter index is out of
     * bounds.
     * @param parameterIndex The index of the desired parameter
     * @return The parameter located at the given index
     */
    public Parameter getParameter(int parameterIndex) {
        return parameters[parameterIndex];
    }

    /**
     * Returns an iterator for this CommandForm's parameters.
     * @return An iterator that iterates over this CommandForm's parameters
     */
    @Override
    public Iterator<Parameter> iterator() {
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
    public MatchResult matches(String[] args) {
        if(args.length == 0) {
            boolean matches = parameters.length == 0;
            return new MatchResult(this, true, matches, matches ? ImmutableTriple.of(true,
                    ArrayUtils.EMPTY_OBJECT_ARRAY, null) : null);
        }

        if(args.length < requiredLength || args.length > parameters.length && !vararg) {
            return new MatchResult(this, true, false, null);
        }

        int iters = Math.max(args.length, parameters.length);
        Object[] result = new Object[iters];

        for(int i = 0; i < iters; i++)
        {
            Parameter parameter = parameters[Math.min(i, parameters.length - 1)];
            String input;
            if(i >= args.length) {
                if(parameter.isOptional()) {
                    input = parameter.getDefaultValue(); //parameter is optional and argument is not supplied
                }
                else {
                    input = StringUtils.EMPTY; //parameter must be vararg but user didn't supply any arguments
                }
            }
            else {
                input = args[i]; //take user argument when possible
            }

            if(!parameter.getPattern().matcher(input).matches()) {
                return new MatchResult(this, true, false, null); //regex matching failed
            }

            ImmutableTriple<Boolean, Object, String> conversionResult = parameter.getConverter().convert(input);

            if(conversionResult.left) { //successful conversion
                result[i] = conversionResult.middle;
            }
            else { //failed conversion
                return new MatchResult(this, true, true, ImmutableTriple.of(false, null,
                        conversionResult.right));
            }
        }

        return new MatchResult(this, true,true, ImmutableTriple.of(true, result, null));
    }

    /**
     * Returns how many sequential arguments the provided argument array matches. This is used to implement relatively
     * intelligent tab completion.
     * @param args The (potentially incomplete) argument array to match
     * @return The number of matching, sequential arguments
     */
    public int fuzzyMatch(String[] args) {
        if(parameters.length == 0 || args.length > requiredLength && !vararg) {
            return 0;
        }

        int j = 0;
        for(int i = 0; i < args.length; i++)
        {
            if(parameters[Math.min(parameters.length - 1, i)].getPattern().matcher(args[i]).matches()) {
                j++;
            }
            else {
                break;
            }
        }

        return j;
    }

    /**
     * Returns a short, user-friendly string that should explain what the form does.
     * @return A user-friendly string explaining what the CommandForm does
     */
    public String getUsage() {
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
     * If false, the text returned by execute() will be treated as plaintext. If true, it will be stylized.
     * @return Whether or not this CommandForm stylizes
     */
    public boolean canStylize() { return false; }

    /**
     * Gets the argument completer object that is used to tab complete an argument array that partially matches this
     * form.
     * @return The tab completer to use
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
    public abstract CommandValidator getValidator(Context context, Object[] arguments);

    /**
     * Runs the command after the conversion and validation steps have been performed. The Object[] array passed to
     * this method will always be the same as those passed to getValidator.
     * @param context The current context
     * @param arguments An array of converted values, whose types correspond to any converters defined within the
     *                  parameters array
     * @return A message that will be displayed to the player, and formatted if getStylizer() doesn't return null
     */
    public abstract String execute(Context context, Object[] arguments);
}