package io.github.regularcommands.validator;

public final class ValidationResult {
    private final boolean valid;
    private final String errorMessage;

    private ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = valid ? null : errorMessage;
    }

    /**
     * Create a new ValidationResult.
     * @param valid Whether or not this ValidationResult is valid
     * @param errorMessage The error message, which will only be used if !valid
     * @return The new ValidationResult object
     */
    public static ValidationResult of(boolean valid, String errorMessage) {
        return new ValidationResult(valid, errorMessage);
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
    public String getErrorMessage() {
        return errorMessage;
    }
}
