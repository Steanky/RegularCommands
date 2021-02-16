package io.github.regularcommands.validator;

import io.github.regularcommands.commands.Context;

/**
 * Functional interface defining an object that may test command context.
 * @param <T> The type of data this ValidationResult may return
 * @param <V> The type of data that was produced by a previously chained validator
 */
public interface ValidationStep<T, V> {
    /**
     * Defines a specific validation step.
     * @param context The command context
     * @param arguments The command arguments
     * @return A ValidationResult object containing the result of this validation
     */
    ValidationResult<T> validate(Context context, Object[] arguments, V previousData);
}
