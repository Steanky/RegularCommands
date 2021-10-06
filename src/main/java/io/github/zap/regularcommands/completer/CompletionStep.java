package io.github.zap.regularcommands.completer;

import io.github.zap.regularcommands.commands.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CompletionStep {
    List<String> complete(@NotNull Context context, @NotNull String[] args);
}
