package io.github.zap.regularcommands.completer;

import io.github.zap.regularcommands.commands.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This object completes arguments
 */
public class ArgumentCompleter {
    private final CompletionStep step;
    private final ArgumentCompleter depend;

    /**
     * Creates a new ArgumentCompleter object with the specified CompletionStep, an ArgumentCompleter this instance
     * should be chained to.
     * @param step The completion step
     * @param depend The ArgumentCompleter whose output will be added to this instance's
     */
    public ArgumentCompleter(@NotNull CompletionStep step, @Nullable ArgumentCompleter depend) {
        this.step = step;
        this.depend = depend;
    }

    /**
     * Creates a new ArgumentCompleter object with the specified CompletionStep.
     * @param step The completion step
     */
    public ArgumentCompleter(@NotNull CompletionStep step) {
        this(step, null);
    }

    /**
     * Produces a list of completion strings given the context, CommandForm, and a possibly incomplete set of arguments.
     * @param context The command context
     * @param args A potentially incomplete list of arguments
     * @return A list of strings corresponding to potential completions, or null if there are none
     */
    public List<String> complete(@NotNull Context context, @NotNull String[] args) {
        if(depend == null) {
            return step.complete(context, args);
        }

        List<String> nextResult = depend.complete(context, args);
        List<String> result = step.complete(context, args);

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
