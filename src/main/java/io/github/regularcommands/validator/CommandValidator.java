package io.github.regularcommands.validator;

import io.github.regularcommands.commands.Context;

import java.util.Objects;

/**
 * Used to validate against the command context. Validators can 'depend' on the success of a single other validator,
 * which will be executed first.
 *
 * Validators support covariance. If A is a superclass of B, and Validator 1 depends on validator of type A, Validator 1
 * can be constructed with a validator of type B.
 * @param <T> The type of data object this CommandValidator produces
 * @param <V> The type of data object this CommandValidator receives
 */
public class CommandValidator<T, V> {
   /**
    * Enables covariant validators.
    * @param <U> The subclass of V (or V itself)
    */
   private class Holder<U extends V> {
      private final ValidationStep<T, U> step;
      private final CommandValidator<U, ?> depend;

      private Holder(ValidationStep<T, U> step, CommandValidator<U, ?> depend) {
         this.step = step;
         this.depend = depend;
      }

      private ValidationResult<T> validate(Context context, Object[] arguments) {
         if(depend == null) {
            return step.validate(context, arguments, null);
         }

         ValidationResult<U> result = depend.validate(context, arguments);

         if(result.isValid()) {
            return step.validate(context, arguments, result.getData());
         }

         return ValidationResult.of(false, result.getErrorMessage(), null);
      }
   }

   private final Holder<? extends V> holder;

   /**
    * Creates a new CommandValidator instance that depends on the success of another validator, which will be tested
    * first. If it fails, this CommandValidator will not execute.
    * @param step The ValidationStep used by this validator. This is the code that will perform the actual, contextual
    *             testing
    * @param depend The CommandValidator whose success determines whether this instances gets tested or not
    */
   public <U extends V> CommandValidator(ValidationStep<T, U> step, CommandValidator<U, ?> depend) {
      holder = new Holder<>(Objects.requireNonNull(step, "validation step cannot be null"), depend);
   }

   /**
    * Creates a new CommandValidator instance, which does not depend on any other validators.
    * @param step The ValidationStep used by this validator
    */
   public CommandValidator(ValidationStep<T, ? extends V> step) {
      this(step, null);
   }

   /**
    * Runs validation, given a context and an Object array of arguments. Validators chained last are checked first, and
    * earlier validators will not be executed if later validators fail.
    * @param context The validation context
    * @param arguments The command arguments
    * @return A ValidationResult object indicating the success or failure of this validator.
    */
   public ValidationResult<T> validate(Context context, Object[] arguments) {
      return holder.validate(context, arguments);
   }

   /**
    * Gets the ValidationStep for this instance. Can be used to create a composite CommandValidator in certain
    * circumstances.
    * @return The ValidationStep for this instance
    */
   public ValidationStep<T, ? extends V> getStep() {
      return holder.step;
   }
}