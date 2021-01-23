package io.github.regularcommands.util;

import io.github.regularcommands.completer.ArgumentCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for things related to ArgumentCompleters. Includes default ArgumentCompleters that perform general
 * tasks.
 *
 * PARAMETER_COMPLETER: Performs tab completion based off of the tab completion options specified in each parameter.
 */
public final class Completers {
    public static final ArgumentCompleter PARAMETER_COMPLETER = new ArgumentCompleter((context, form, args) -> {
        int length = form.length();

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
