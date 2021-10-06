package io.github.zap.regularcommands.commands;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface PageBuilder {
    void addEntry(@NotNull CommandForm<?> form);

    @NotNull Component getPage(int index);

    int pageCount();
}
