package io.github.regularcommands.validator;

import io.github.regularcommands.commands.CommandForm;
import io.github.regularcommands.commands.Context;
import io.github.regularcommands.util.Validators;

import java.util.Objects;

/**
 * Used to validate against the command context. Validators can 'depend' on the success of a single other validator,
 * which will be executed first.
 */
public class CommandValidator {
   private final ValidationStep step;
   private final CommandValidator depend;

   /**
    * Creates a new CommandValidator instance that depends on the success of another validator, which will be tested
    * first. If it fails, this CommandValidator will not execute.
    * @param step The ValidationStep used by this validator. This is the code that will perform the actual, contextual
    *             testing
    * @param depend The CommandValidator whose success determines whether this instances gets tested or not
    */
   public CommandValidator(ValidationStep step, CommandValidator depend) {
      this.step = Objects.requireNonNull(step, "validation step cannot be null");
      this.depend = depend;
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
    * @return A ValidationResult object indicating the success or failure of this validator.
    */
   public ValidationResult validate(Context context, CommandForm form, Object[] arguments) {
      if(depend == null) {
         return step.validate(context, form, arguments);
      }

      ValidationResult result = depend.validate(context, form, arguments);
      if(result.isValid()) {
         return step.validate(context, form, arguments);
      }

      return result;
   }

   /**
    * Gets the ValidationStep for this instance. Can be used to create a composite CommandValidator in certain
    * circumstances.
    * @return The ValidationStep for this instance
    */
   public ValidationStep getStep() {
      return step;
   }
}