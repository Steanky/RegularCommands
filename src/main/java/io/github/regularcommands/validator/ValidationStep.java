package io.github.regularcommands.validator;

import io.github.regularcommands.commands.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Functional interface defining an object that may test command context.
 */
public interface ValidationStep {
    /**
     * Defines a specific validation step.
     * @param context The command context
     * @param arguments The command arguments
     * @return An ImmutablePair object whose left object indicates the success of the validation and whose right object
     * contains a user-friendly error message that should be null if the left boolean is true
     */
    Pair<Boolean, String> validate(Context context, Object[] arguments);
}
