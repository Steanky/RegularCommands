package io.github.regularcommands.validator;

import net.kyori.adventure.text.Component;

import java.util.Objects;

/**
 * Encapsulates the result of validating an object.
 * @param <T> The type of metadata held by this ValidationResult, which may contain the result of a cast or some other
 *           operation.
 */
public final class ValidationResult<T> {
    private final boolean valid;
    private final Component errorMessage;
    private final T data;

    private ValidationResult(boolean valid, Component errorMessage, T data) {
        if(!valid && data != null) {
            throw new IllegalArgumentException("data cannot be non-null if the ValidationResult is invalid");
        }

        this.valid = valid;
        this.errorMessage = valid ? null : Objects.requireNonNull(errorMessage, "error message cannot be null" +
                " when invalid");
        this.data = data;
    }

    /**
     * Create a new ValidationResult.
     * @param valid Whether or not this ValidationResult is valid
     * @param errorMessage The error message, which will only be used if !valid
     * @return The new ValidationResult object
     */
    public static <T> ValidationResult<T> of(boolean valid, Component errorMessage, T data) {
        return new ValidationResult<>(valid, errorMessage, data);
    }

    /**
     * Whether or not the ValidationResult is valid. If true, getErrorMessage() will be null.
     * @return Whether or not this ValidationResult is valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * The error message that should describe why command validation failed.
     * @return The error message if isValid() returns false, null if it returns true
     */
    public Component getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the data object that may have been created as a result of validation. This is guaranteed to be null if
     * isValid() returns false, and may or may not be null if it returns true.
     * @return The data, or null if !valid (or the validator did not assign a value)
     */
    public T getData() {
        return data;
    }
}
