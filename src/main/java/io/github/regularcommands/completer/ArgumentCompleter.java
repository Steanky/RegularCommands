package io.github.regularcommands.completer;

import io.github.regularcommands.commands.CommandForm;
import io.github.regularcommands.commands.Context;

import java.util.List;

public final class ArgumentCompleter {
    private final CompletionStep step;
    private ArgumentCompleter next;

    /**
     * Creates a new ArgumentCompleter object with the specified CompletionStep.
     * @param step The argument completion step
     */
    public ArgumentCompleter(CompletionStep step) {
        this.step = step;
    }

    /**
     * Makes this ArgumentCompleter execute another ArgumentCompleter (the result is additive).
     * @param next The ArgumentCompleter that should execute before this one
     * @return The ArgumentCompleter supplied to parameter 'next'
     */
    public ArgumentCompleter chain(ArgumentCompleter next) {
        this.next = next;
        return next;
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

        List<String> result = next.complete(context, form, args);
        List<String> stepResult = step.complete(context, form, args);

        if(stepResult != null) {
            if(result != null) {
                result.addAll(stepResult);
            }
            else {
                result = stepResult;
            }
        }

        return result;
    }
}
