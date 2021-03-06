package io.github.regularcommands.completer;

import io.github.regularcommands.commands.Context;

import java.util.List;

public interface CompletionStep {
    List<String> complete(Context context, String[] args);
}
