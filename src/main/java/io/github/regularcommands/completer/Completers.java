package io.github.regularcommands.completer;

import io.github.regularcommands.converter.Parameter;

import java.util.ArrayList;
import java.util.List;

public final class Completers {
    public static final ArgumentCompleter PARAMETER_COMPLETER = new ArgumentCompleter((context, form, args) -> {
        Parameter[] parameters = form.getParameters();

        if(parameters.length > 0) {
            List<String> options = parameters[Math.min(parameters.length - 1, args.length - 1)].getTabCompletionOptions();
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
    }, false);
}
