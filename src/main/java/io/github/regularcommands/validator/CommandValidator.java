package io.github.regularcommands.validator;

import io.github.regularcommands.commands.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;

/**
 * Used to validate against the command context. Validators can 'depend' on the success of a single other validator,
 * which will be executed first.
 */
public class CommandValidator {
   private final ValidationStep step;
   private CommandValidator next;

   /**
    * Creates a new CommandValidator instance that depends on the success of another validator, which will be tested
    * first. If it fails, this CommandValidator will not execute.
    * @param step The ValidationStep used by this validator. This is the code that will perform the actual, contextual
    *             testing
    * @param dependOn The CommandValidator whose success determines whether this instances gets tested or not
    */
   public CommandValidator(ValidationStep step, CommandValidator dependOn) {
      this.step = Objects.requireNonNull(step, "validation step cannot be null");
   }

   /**
    * Creates a new CommandValidator instance, which does not depend on any other validators.
    * @param step The ValidationStep used by this validator
    */
   public CommandValidator(ValidationStep step) {
      this(step, null);
   }

   /**
    * Runs validation, given a context and an Object array of arguments. Validators chained last are checked first, and
    * earlier validators will not be executed if later validators fail.
    * @param context The validation context
    * @param arguments The command arguments
    * @return An ImmutablePair object whose left object indicates the success of the validation and whose right object
    * contains a user-friendly error message that should be null if the left boolean is true
    */
   public Pair<Boolean, String> validate(Context context, Object[] arguments) {
      if(next == null) {
         return step.validate(context, arguments);
      }

      Pair<Boolean, String> result = next.validate(context, arguments);
      if(result.getLeft()) {
         return step.validate(context, arguments);
      }

      return result;
   }
}