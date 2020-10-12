package io.github.regularcommands.validator;

import io.github.regularcommands.commands.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Objects;

/**
 * Used to validate against the command context. Can be chained with other validators to test complex scenarios in a
 * simple way. Validators chained last are checked first.
 */
public class CommandValidator {
   private final ValidationStep step;
   private CommandValidator next;

   /**
    * Creates a new CommandValidator instance.
    * @param step The IValidationStep used by this validator
    */
   public CommandValidator(ValidationStep step) {
      this.step = Objects.requireNonNull(step, "validation step cannot be null");
   }

   /**
    * Makes the execution of this validator 'dependent on' the success of the chained validator.
    * @param add The CommandValidator that will be executed before this one
    * @return The CommandValidator that was passed to the add parameter
    */
   public final CommandValidator chain(CommandValidator add) {
      next = add;
      return add;
   }

   /**
    * Runs validation, given a context and an Object array of arguments. Validators chained last are checked first, and
    * earlier validators will not be executed if later validators fail.
    * @param context The validation context
    * @param arguments The command arguments
    * @return An ImmutablePair object whose left object indicates the success of the validation and whose right object
    * contains a user-friendly error message that should be null if the left boolean is true
    */
   public final ImmutablePair<Boolean, String> validate(Context context, Object[] arguments) {
      if(next == null) {
         return step.validate(context, arguments);
      }

      ImmutablePair<Boolean, String> result = next.validate(context, arguments);
      if(result.left) {
         return step.validate(context, arguments);
      }

      return result;
   }
}