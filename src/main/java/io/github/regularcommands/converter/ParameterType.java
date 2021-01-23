package io.github.regularcommands.converter;

/**
 * Defines the type of a parameter. This can be either STANDARD, SIMPLE, VARARG, or OPTIONAL.
 *
 * STANDARD parameters are regex-validated and correspond to a single argument. They do not provide a default value.
 *
 * SIMPLE parameters are equality-validated and correspond to a single argument. They do not provide a default value.
 *
 * VARARG parameters are regex-validated and apply to any number of arguments after they are reached. They do not
 * provide a default value.
 *
 * OPTIONAL parameters are regex-validated and apply to one argument. They provide a default value if their argument is
 * not specified.
 */
public enum ParameterType {
    STANDARD,
    SIMPLE,
    VARARG,
    OPTIONAL
}
