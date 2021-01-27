package io.github.regularcommands.validator;

import io.github.regularcommands.commands.Context;

/**
 * Functional interface defining an object that may test command context.
 */
public interface ValidationStep {
    /**
     * Defines a specific validation step.
     * @param context The command context
     * @param arguments The command arguments
     * @return A ValidationResult object containing the result of this validation
     */
    ValidationResult validate(Context context, Object[] arguments);
}
