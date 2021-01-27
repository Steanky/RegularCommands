package io.github.regularcommands.converter;

import java.util.Objects;

/**
 * Encapsulates the result of trying to convert an argument.
 * @param <T> The type of object we converted to
 */
public class ConversionResult<T> {
    private final boolean valid;
    private final T conversion;
    private final String errorMessage;

    private ConversionResult(boolean valid, T conversion, String errorMessage) {
        this.valid = valid;
        this.conversion = valid ? Objects.requireNonNull(conversion, "conversion cannot be null when valid") : null;
        this.errorMessage = valid ? null : Objects.requireNonNull(errorMessage, "error message cannot be null " +
                "when invalid");
    }

    /**
     * Creates a new ConversionResult.
     * @param valid Whether or not this result is valid
     * @param conversion The conversion of this result, which is ignored if !valid
     * @param errorMessage The error message, which is ignored if valid
     * @param <T> The type of the object that was converted
     * @return A new ConversionResult object
     */
    public static <T> ConversionResult<T> of(boolean valid, T conversion, String errorMessage) {
        return new ConversionResult<>(valid, conversion, errorMessage);
    }

    /**
     * Whether or not the ConversionResult is valid. If true, getErrorMessage() will be null.
     * @return Whether or not this ValidationResult is valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Return the converted object. This will be null if !valid.
     * @return The converted object, or null
     */
    public T getConversion() {
        return conversion;
    }

    /**
     * The error message that should describe why conversion failed.
     * @return The error message if isValid() returns false, null if it returns true
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
