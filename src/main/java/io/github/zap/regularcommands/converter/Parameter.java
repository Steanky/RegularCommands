package io.github.zap.regularcommands.converter;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * This is an immutable data container object that is used to define a command parameter. An array of these objects
 * constitutes the entire 'signature' of a CommandForm.
 *
 * There are four types of Parameter objects: STANDARD, SIMPLE, VARARG, or OPTIONAL. Standard parameters are
 * regex-matched, do not supply a default value, and are used to match exactly 1 input argument. Simple parameters
 * are equality matched, do not supply a default value, and are used to match exactly 1 input argument. Vararg
 * parameters are regex-matched, do not supply a default value, and are used to match 0 or more input arguments.
 * Finally, optional parameters are regex-matched, supply a default value, and are used to match 0 or 1 input arguments.
 */
public class Parameter {
    public enum ParameterType {
        STANDARD,
        SIMPLE,
        VARARG,
        OPTIONAL
    }

    private final Pattern pattern;
    private final String match;
    private final Component usage;
    private final ArgumentConverter<Object> converter;
    private final List<String> staticCompletionOptions;

    private final ParameterType type;
    private final String defaultValue;

    @SuppressWarnings("unchecked")
    private Parameter(String definition, Component usage, String defaultValue, ArgumentConverter<?> converter,
                      List<String> staticCompletionOptions, ParameterType type) {
        switch (type) {
            case SIMPLE:
                this.pattern = null;
                this.match = Objects.requireNonNull(definition, "definition cannot be null for ParameterType.SIMPLE");
                this.staticCompletionOptions = List.of(definition);
                this.defaultValue = null;
                break;
            case OPTIONAL:
                this.pattern = Pattern.compile(definition);
                this.match = null;
                this.staticCompletionOptions = staticCompletionOptions;
                this.defaultValue = Objects.requireNonNull(defaultValue, "defaultValue cannot be null for ParameterType.OPTIONAL");
                break;
            case STANDARD:
            case VARARG:
            default:
                this.pattern = Pattern.compile(definition);
                this.match = null;
                this.staticCompletionOptions = staticCompletionOptions;
                this.defaultValue = null;
                break;
        }

        this.usage = Objects.requireNonNull(usage, "usage cannot be null for ParameterType.OPTIONAL");
        this.converter = (ArgumentConverter<Object>) converter;
        this.type = type;
    }

    /**
     * Creates a new optional parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     * @param defaultValue The value that will be used if the user does not supply this parameter
     * @param converter The converter that will be used to convert user input
     * @param staticCompletionOptions A list of completion options that will appear when a user tries to tab complete
     *                                on this parameter
     */
    public Parameter(String regex, Component usage, String defaultValue, ArgumentConverter<?> converter,
                     List<String> staticCompletionOptions) {
        this(regex, usage, defaultValue, converter, staticCompletionOptions, ParameterType.OPTIONAL);
    }

    /**
     * Creates a new optional parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     * @param defaultValue The value that will be used if the user does not supply this parameter
     * @param converter The converter that will be used to convert user input
     */
    public Parameter(String regex, Component usage, String defaultValue, ArgumentConverter<?> converter) {
        this(regex, usage, defaultValue, converter, null, ParameterType.OPTIONAL);
    }

    /**
     * Creates a new optional parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     * @param defaultValue The value that will be used if the user does not supply this parameter
     */
    public Parameter(String regex, Component usage, String defaultValue) {
        this(regex, usage, defaultValue, null, null, ParameterType.OPTIONAL);
    }

    /**
     * Creates a new vararg or standard parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     * @param converter The converter that will be used to convert user input
     * @param staticCompletionOptions A list of completion options that will appear when a user tries to tab complete
     *                                on this parameter
     * @param isVararg Whether or not the parameter is variable-argument (if it can match any number of user arguments)
     */
    public Parameter(String regex, Component usage, ArgumentConverter<?> converter,
                     List<String> staticCompletionOptions, boolean isVararg) {
        this(regex, usage, null, converter, staticCompletionOptions, isVararg ? ParameterType.VARARG :
                ParameterType.STANDARD);
    }

    /**
     * Creates new a standard parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     * @param converter The converter that will be used to convert user input
     * @param staticCompletionOptions A list of completion options that will appear when a user tries to tab complete
     *                                on this parameter
     */
    public Parameter(String regex, Component usage, ArgumentConverter<?> converter, List<String> staticCompletionOptions) {
        this(regex, usage, null, converter, staticCompletionOptions, ParameterType.STANDARD);
    }

    /**
     * Creates a new vararg or standard parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     * @param converter The converter that will be used to convert user input
     * @param isVararg Whether or not the parameter is variable-argument (if it can match any number of user arguments)
     */
    public Parameter(String regex, Component usage, ArgumentConverter<?> converter, boolean isVararg) {
        this(regex, usage, null, converter, null, isVararg ? ParameterType.VARARG :
                ParameterType.STANDARD);
    }

    /**
     * Creates a standard parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     * @param converter The converter that will be used to convert user input
     */
    public Parameter(String regex, Component usage, ArgumentConverter<?> converter) {
        this(regex, usage, null, converter, null, ParameterType.STANDARD);
    }

    /**
     * Creates a new vararg or standard parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     * @param isVararg Whether or not the parameter is variable-argument (if it can match any number of user arguments)
     */
    public Parameter(String regex, Component usage, boolean isVararg) {
        this(regex, usage, null, null, null, isVararg ?
                ParameterType.VARARG : ParameterType.STANDARD);
    }

    /**
     * Creates a new standard parameter.
     * @param regex The regex used to match this parameter
     * @param usage The usage of this parameter, which should explain what the parameter does in a few words. Should be
     *              formatted something like this:
     *
     *              [example-name]
     *              [example_name]
     *              [test name]
     *
     *              Parameter usages are delimited by spaces. Include brackets or quotation marks to avoid confusing
     *              users.
     */
    public Parameter(String regex, Component usage) {
        this(regex, usage, null, null, null, ParameterType.STANDARD);
    }

    /**
     * Creates a new simple parameter.
     * along with a converter to convert user input.
     * @param match The exact string to match, which is case-sensitive
     * @param converter The converter used to convert this argument
     */
    public Parameter(String match, ArgumentConverter<?> converter) {
        this(match, null, null, converter, null, ParameterType.SIMPLE);
    }

    /**
     * Creates a new simple parameter.
     * @param match The exact string to match, which is case-sensitive
     */
    public Parameter(String match) {
        this(match, null, null, null, null, ParameterType.SIMPLE);
    }

    /**
     * Gets the converter used to transform the argument string.
     * @return This parameter's associated converter
     */
    public ArgumentConverter<?> getConverter() {
        return converter;
    }

    /**
     * Gets the Pattern used to match input arguments.
     * @return The pattern used to test user input. This will be null if this Parameter is simple
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Returns the string used to perform a simple equality check on the argument.
     * @return If this Parameter is simple, a String that should be used to check for equality with the argument
     */
    public String getMatch() { return match; }

    /**
     * Gets the usage string for this parameter.
     * @return A usage string for this parameter
     */
    public @NotNull Component getUsage() { return usage; }

    /**
     * Gets a copy of the static tab completion options for this parameter.
     * @return The built-in tab completion options that should be shown for this parameter
     */
    public @NotNull List<String> getStaticCompletionOptions() {
        return staticCompletionOptions == null ? new ArrayList<>() : new ArrayList<>(staticCompletionOptions);
    }

    /**
     * Returns the type of this parameter.
     * @return the type of this parameter
     */
    public @NotNull ParameterType getType() { return type; }

    /**
     * Gets the default value of the parameter, or null if there is none defined.
     * @return The default value of the parameter, or null if there is none defined
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}