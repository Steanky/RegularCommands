package io.github.zap.regularcommands.util;

import io.github.zap.regularcommands.commands.CommandForm;
import io.github.zap.regularcommands.completer.ArgumentCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for things related to ArgumentCompleters. Includes a default completer that looks at the form's
 * static completion options and narrows them down based on what the user is typing (last argument).
 */
public final class Completers {
    public static final ArgumentCompleter PARAMETER_COMPLETER = new ArgumentCompleter((context, args) -> {
        CommandForm<?> form = context.getForm();
        int length = form.size();

        if(length > 0) {
            List<String> options = form.getParameter(Math.min(length - 1, args.length - 1)).getStaticCompletionOptions();
            String startsWith = args[args.length - 1];

            List<String> results = new ArrayList<>();
            for(String option : options) {
                if(option.startsWith(startsWith)) {
                    results.add(option);
                }
            }

            return results.size() == 0 ? null : results;
        }

        return null;
    });
}
