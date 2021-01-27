package io.github.regularcommands.converter;

public interface ArgumentConverter<T> {
    /**
     * Converts the argument into a type of object, returning information about the success of the conversion, the
     * object itself, and a user-friendly error message in the event that the conversion fails.
     * @param argument The argument to be converted
     * @return A ConversionResult object representing the result of the conversion
     */
    ConversionResult<T> convert(String argument);
}
