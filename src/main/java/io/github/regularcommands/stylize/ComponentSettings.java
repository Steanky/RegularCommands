package io.github.regularcommands.stylize;

import net.md_5.bungee.api.chat.TextComponent;

public interface ComponentSettings {
    /**
     * Used to apply text effects to a TextComponent object.
     * @param in The text component to transform
     */
    void apply(TextComponent in);
}
