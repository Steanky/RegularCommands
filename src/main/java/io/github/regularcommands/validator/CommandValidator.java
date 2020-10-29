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
   private final boolean mutable;

   /**
    * Creates a new CommandValidator instance with the specified mutability. Immutable validators will throw an
    * exception if an attempt is made to chain another validator to this one.
    * @param step The IValidationStep used by this validator
    * @param mutable Whether or not this validator is mutable
    */
   public CommandValidator(ValidationStep step, boolean mutable) {
      this.step = Objects.requireNonNull(step, "validation step cannot be null");
      this.mutable = mutable;
   }

   /**
    * Creates a new mutable CommandValidator instance.
    * @param step The IValidationStep used by this validator
    */
   public CommandValidator(ValidationStep step) {
      this(step, true);
   }

   /**
    * Makes the execution of this validator 'dependent on' the success of the chained validator. This will throw
    * an exception if the validator is immutable.
    * @param add The CommandValidator that will be executed before this one
    * @return The CommandValidator that was passed to the add parameter
    */
   public CommandValidator chain(CommandValidator add) {
      if(mutable) {
         next = add;
         return add;
      }
      else {
         throw new IllegalStateException("Cannot chain to immutable validators");
      }
   }

   /**
    * Runs validation, given a context and an Object array of arguments. Validators chained last are checked first, and
    * earlier validators will not be executed if later validators fail.
    * @param context The validation context
    * @param arguments The command arguments
    * @return An ImmutablePair object whose left object indicates the success of the validation and whose right object
    * contains a user-friendly error message that should be null if the left boolean is true
    */
   public ImmutablePair<Boolean, String> validate(Context context, Object[] arguments) {
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