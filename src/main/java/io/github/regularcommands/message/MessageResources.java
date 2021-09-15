package io.github.regularcommands.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public interface MessageResources {
    @NotNull TranslatableComponent namedComponent(@NotNull MessageKey key);

    void registerComponent(@NotNull MessageKey key, @NotNull TranslatableComponent component);

    boolean hasKey(@NotNull MessageKey key);
}
