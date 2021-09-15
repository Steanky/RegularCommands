package io.github.regularcommands.converter;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Encapsulates the result of trying to convert an argument.
 * @param <T> The type of object we converted to
 */
public class ConversionResult<T> {
    private final boolean valid;
    private final T conversion;
    private final Component errorMessage;

    private ConversionResult(boolean valid, @Nullable T conversion, @Nullable Component errorMessage) {
        this.valid = valid;
        this.conversion = valid ? Objects.requireNonNull(conversion, "conversion cannot be null when valid") : null;
        this.errorMessage = valid ? null : Objects.requireNonNull(errorMessage, "error message cannot be null " +
                "when invalid");
    }

    /**
     * Creates a new ConversionResult.
     * @param valid True if this result is valid, false otherwise
     * @param conversion The conversion of this result, which is ignored if !valid
     * @param errorMessage The error message, which is ignored if valid
     * @param <T> The type of the object that was converted
     * @return A new ConversionResult object
     */
    public static <T> ConversionResult<T> of(boolean valid, @Nullable T conversion, @Nullable Component errorMessage) {
        return new ConversionResult<>(valid, conversion, errorMessage);
    }

    /**
     * Determine if the ConversionResult is valid. If true, getErrorMessage() will be null.
     * @return True if this ValidationResult is valid, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Return the converted object. This will be null if we're not valid.
     * @return The converted object, or null
     */
    public T getConversion() {
        return conversion;
    }

    /**
     * The error message that should describe why conversion failed.
     * @return The error message if isValid() returns false, null if it returns true
     */
    public Component getErrorMessage() {
        return errorMessage;
    }
}
