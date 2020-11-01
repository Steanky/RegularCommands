package io.github.regularcommands.completer;

import io.github.regularcommands.commands.CommandForm;
import io.github.regularcommands.commands.Context;

import java.util.List;

public class ArgumentCompleter {
    private final CompletionStep step;
    private ArgumentCompleter next;
    private final boolean mutable;

    /**
     * Creates a new ArgumentCompleter object with the specified CompletionStep and mutability. If the mutable field
     * is set to false, this ArgumentCompleter instance will throw an exception if an attempt is made to chain another
     * validator to it.
     * @param step The argument completion step
     * @param mutable Whether or not the ArgumentCompleter is mutable
     */
    public ArgumentCompleter(CompletionStep step, boolean mutable) {
        this.step = step;
        this.mutable = mutable;
    }

    /**
     * Creates a new mutable ArgumentCompleter object with the specified CompletionStep.
     * @param step The argument completion step
     */
    public ArgumentCompleter(CompletionStep step) {
        this(step, true);
    }

    /**
     * Makes this ArgumentCompleter execute another ArgumentCompleter (the result is additive).
     * @param next The ArgumentCompleter that should execute before this one
     * @return The ArgumentCompleter supplied to parameter 'next'
     */
    public ArgumentCompleter chain(ArgumentCompleter next) {
        if(mutable) {
            this.next = next;
            return next;
        }
        else {
            throw new IllegalStateException("Cannot chain to immutable ArgumentCompleters");
        }
    }

    /**
     * Produces a list of completion strings given the context, CommandForm, and a possibly incomplete set of arguments.
     * @param context The command context
     * @param form A partially matching command form
     * @param args A potentially incomplete list of arguments
     * @return A list of strings corresponding to potential completions, or null if there are none
     */
    public List<String> complete(Context context, CommandForm form, String[] args) {
        if(next == null) {
            return step.complete(context, form, args);
        }

        List<String> nextResult = next.complete(context, form, args);
        List<String> result = step.complete(context, form, args);

        if(result != null) {
            if(nextResult != null) {
                nextResult.addAll(result);
            }
            else {
                nextResult = result;
            }
        }

        return nextResult;
    }
}
