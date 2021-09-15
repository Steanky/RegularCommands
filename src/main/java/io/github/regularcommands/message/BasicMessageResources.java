package io.github.regularcommands.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BasicMessageResources implements MessageResources {
    private final Map<String, TranslatableComponent> components = new HashMap<>();

    @Override
    public @NotNull TranslatableComponent namedComponent(@NotNull MessageKey key) {
        String keyName = key.key();
        return components.computeIfAbsent(keyName, (k) -> {
            throw new IllegalArgumentException("component named " + k + " does not exist");
        });
    }

    @Override
    public void registerComponent(@NotNull MessageKey key, @NotNull TranslatableComponent component) {
        String keyName = key.key();
        if(components.containsKey(keyName)) {
            throw new IllegalArgumentException("component named " + keyName + " already exists");
        }

        components.put(keyName, component);
    }

    @Override
    public boolean hasKey(@NotNull MessageKey key) {
        return components.containsKey(key.key());
    }
}
