package io.github.regularcommands.converter;

import com.google.common.collect.Lists;
import io.github.regularcommands.util.Converters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is an immutable data container object that is used to define a command parameter. An array of these objects
 * constitutes the entire 'signature' of a CommandForm.
 */
public class Parameter {
    private final Pattern pattern;
    private final String match;
    private final String usage;
    private final ArgumentConverter<Object> converter;
    private final List<String> staticCompletionOptions;

    private final ParameterType type;
    private final String defaultValue;

    private Parameter(String definition, String usage, String defaultValue, ArgumentConverter<?> converter,
                      List<String> staticCompletionOptions, ParameterType type) {
        switch (type) {
            case SIMPLE:
                this.pattern = null;
                this.match = definition;
                this.usage = usage == null ? '[' + definition + ']' : usage;
                this.staticCompletionOptions = Lists.newArrayList(definition);
                break;
            case OPTIONAL:
                if(defaultValue == null) {
                    throw new IllegalArgumentException("when using ParameterType.OPTIONAL, must supply non-null " +
                            "default value");
                }
            case STANDARD:
            case VARARG:
            default:
                this.pattern = Pattern.compile(definition);
                this.match = null;

                if(usage == null) {
                    throw new IllegalArgumentException("usage cannot be null except for ParameterType.SIMPLE");
                }

                this.usage = usage;
                this.staticCompletionOptions = staticCompletionOptions;
                break;
        }

        this.converter = Converters.asObjectConverter(converter);
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public Parameter(String definition, String usage, String defaultValue, ArgumentConverter<?> converter,
                     List<String> staticCompletionOptions) {
        this(definition, usage, defaultValue, converter, staticCompletionOptions, ParameterType.OPTIONAL);
    }

    public Parameter(String definition, String usage, String defaultValue, ArgumentConverter<?> converter) {
        this(definition, usage, defaultValue, converter, null, ParameterType.OPTIONAL);
    }

    public Parameter(String definition, String usage, String defaultValue) {
        this(definition, usage, defaultValue, null, null, ParameterType.OPTIONAL);
    }

    public Parameter(String definition, String usage, ArgumentConverter<?> converter, List<String> staticCompletionOptions,
                     ParameterType type) {
        this(definition, usage, null, converter, staticCompletionOptions, type);
    }

    public Parameter(String definition, String usage, ArgumentConverter<?> converter, ParameterType type) {
        this(definition, usage, null, converter, null, type);
    }

    public Parameter(String definition, String usage, ParameterType type) {
        this(definition, usage, null, null, null, type);
    }

    public Parameter(String definition, ParameterType type) {
        this(definition, null, null, null, null, type);
    }

    public Parameter(String match) {
        this(match, null, null, null, null, ParameterType.SIMPLE);
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
    public String getUsage() { return usage; }

    /**
     * Gets a copy of the static tab completion options for this parameter.
     * @return The built-in tab completion options that should be shown for this parameter
     */
    public List<String> getStaticCompletionOptions() {
        return new ArrayList<>(staticCompletionOptions);
    }

    /**
     * Returns the type of this parameter.
     * @return the type of this parameter
     */
    public ParameterType getType() { return type; }

    /**
     * Gets the default value of the parameter, or null if there is none defined.
     * @return The default value of the parameter, or null if there is none defined
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}