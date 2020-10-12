package io.github.regularcommands.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * This is an immutable data container object that is used to define a command parameter. An array of these objects
 * constitutes the entire 'signature' of a CommandForm.
 */
public class Parameter {
    private final Pattern pattern;
    private final String usage;
    private final ArgumentConverter<Object> converter;
    private final List<String> tabCompletionOptions;

    private final boolean isVararg;
    private final boolean isOptional;
    private final String defaultValue;

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param converter The converter used to transform the input string
     * @param tabCompletionOptions The static completion options that are shown to the user
     */
    public Parameter(String regex, String usage, ArgumentConverter<?> converter, List<String> tabCompletionOptions) {
        this.pattern = Pattern.compile(regex);
        this.usage = Objects.requireNonNull(usage, "usage cannot be null");
        this.converter = Converters.asObjectConverter(Objects.requireNonNull(converter, "converter cannot be null"));
        this.tabCompletionOptions = Objects.requireNonNull(tabCompletionOptions, "tabCompletionOptions cannot be null");

        isVararg = false;
        isOptional = false;
        defaultValue = null;
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param converter The converter used to transform the input string
     */
    public Parameter(String regex, String usage, ArgumentConverter<?> converter) {
        this(regex, usage, converter, new ArrayList<>());
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param tabCompletionOptions The static completion options that are shown to the user
     */
    public Parameter(String regex, String usage, List<String> tabCompletionOptions) {
        this(regex, usage, Converters.STRING_CONVERTER, tabCompletionOptions);
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     */
    public Parameter(String regex, String usage) {
        this(regex, usage, Converters.STRING_CONVERTER, new ArrayList<>());
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param converter The converter used to transform the input string
     * @param tabCompletionOptions The static completion options that are shown to the user
     * @param defaultValue The default value that is used when the user does not type any argument
     */
    public Parameter(String regex, String usage, ArgumentConverter<?> converter, List<String> tabCompletionOptions,
                     String defaultValue) {
        this.pattern = Pattern.compile(regex);
        this.usage = Objects.requireNonNull(usage, "usage cannot be null");
        this.converter = Converters.asObjectConverter(Objects.requireNonNull(converter, "converter cannot be null"));
        this.tabCompletionOptions = Objects.requireNonNull(tabCompletionOptions, "tab completions cannot be null");
        this.defaultValue = Objects.requireNonNull(defaultValue, "default value cannot be null");

        isVararg = false;
        isOptional = true;
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param converter The converter used to transform the input string
     * @param defaultValue The default value that is used when the user does not type any argument
     */
    public Parameter(String regex, String usage, ArgumentConverter<?> converter, String defaultValue) {
        this(regex, usage, converter, new ArrayList<>(), defaultValue);
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param defaultValue The default value that is used when the user does not type any argument
     */
    public Parameter(String regex, String usage, String defaultValue) {
        this(regex, usage, Converters.STRING_CONVERTER, new ArrayList<>(), defaultValue);
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param tabCompletionOptions The static completion options that are shown to the user
     * @param defaultValue The default value that is used when the user does not type any argument
     */
    public Parameter(String regex, String usage, List<String> tabCompletionOptions, String defaultValue) {
        this(regex, usage, Converters.STRING_CONVERTER, tabCompletionOptions, defaultValue);
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param converter The converter used to transform the input string
     * @param tabCompletionOptions The static completion options that are shown to the user
     * @param isVararg Defines whether or not this parameter is considered variable-length, that is, whether or not this
     *                 parameter will be used to match and convert any number of subsequent arguments
     */
    public Parameter(String regex, String usage, ArgumentConverter<?> converter, List<String> tabCompletionOptions,
                     boolean isVararg) {
        this.pattern = Pattern.compile(regex);
        this.usage = Objects.requireNonNull(usage, "usage cannot be null");
        this.converter = Converters.asObjectConverter(Objects.requireNonNull(converter, "converter cannot be null"));
        this.tabCompletionOptions = Objects.requireNonNull(tabCompletionOptions, "tab completions cannot be null");
        this.isVararg = isVararg;

        isOptional = false;
        defaultValue = null;
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param converter The converter used to transform the input string
     * @param isVararg Defines whether or not this parameter is considered variable-length, that is, whether or not this
     *                 parameter will be used to match and convert any number of subsequent arguments
     */
    public Parameter(String regex, String usage, ArgumentConverter<?> converter, boolean isVararg) {
        this(regex, usage, converter, new ArrayList<>(), isVararg);
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param tabCompletionOptions The static completion options that are shown to the user
     * @param isVararg Defines whether or not this parameter is considered variable-length, that is, whether or not this
     *                 parameter will be used to match and convert any number of subsequent arguments
     */
    public Parameter(String regex, String usage, List<String> tabCompletionOptions, boolean isVararg) {
        this(regex, usage, Converters.STRING_CONVERTER, tabCompletionOptions, isVararg);
    }

    /**
     * Creates a new Parameter object, which defines a regex pattern that command user input arguments are matched
     * against.
     * @param regex The pattern used to match user input
     * @param usage The 'usage' of the parameter, which should generally be a single descriptive word
     * @param isVararg Defines whether or not this parameter is considered variable-length, that is, whether or not this
     *                 parameter will be used to match and convert any number of subsequent arguments
     */
    public Parameter(String regex, String usage, boolean isVararg) {
        this(regex, usage, Converters.STRING_CONVERTER, new ArrayList<>(), isVararg);
    }

    /**
     * Gets the converter used to transform the argument string.
     * @return This parameter's associated converter
     */
    public ArgumentConverter<Object> getConverter() {
        return converter;
    }

    /**
     * Gets the Pattern used to match input arguments.
     * @return The pattern used to test user input
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Gets the usage string for this parameter.
     * @return A usage string for this parameter
     */
    public String getUsage() { return usage; }

    /**
     * Gets the static tab completion options for this parameter.
     * @return The built-in tab completion options that should be shown for this parameter
     */
    public List<String> getTabCompletionOptions() {
        return new ArrayList<>(tabCompletionOptions);
    }

    /**
     * Returns whether or not this Parameter is optional (has a default value)
     * @return true if this parameter is optional, false otherwise
     */
    public boolean isOptional() {
        return isOptional;
    }

    /**
     * Gets whether or not this Parameter is variable-length (whether it will be used to match and convert any number
     * of subsequent arguments)
     * @return true if this parameter is vararg, false otherwise
     */
    public boolean isVararg() { return isVararg; }

    /**
     * Gets the default value of the parameter, or null if there is none defined.
     * @return The default value of the parameter, or null if there is none defined
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}