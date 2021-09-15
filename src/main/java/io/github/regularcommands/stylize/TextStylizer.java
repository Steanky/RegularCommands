package io.github.regularcommands.stylize;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;

import java.util.*;

/**
 * This is used to store stylization information.
 */
@Deprecated
public class TextStylizer {
    private final Map<String, ComponentSettings> formatters;

    public TextStylizer() {
        formatters = new HashMap<>();

        formatters.put("reset", in -> {
            in.setColor(ChatColor.WHITE);
            in.setBold(false);
            in.setObfuscated(false);
            in.setStrikethrough(false);
            in.setUnderlined(false);
            in.setItalic(false);
        });

        formatters.put("url", in -> {
            in.setColor(ChatColor.BLUE);
            in.setBold(true);
            in.setUnderlined(true);
        });

        formatters.put("blue", in -> in.setColor(ChatColor.BLUE));
        formatters.put("red", in -> in.setColor(ChatColor.RED));
        formatters.put("white", in -> in.setColor(ChatColor.WHITE));
        formatters.put("aqua", in -> in.setColor(ChatColor.AQUA));
        formatters.put("black", in -> in.setColor(ChatColor.BLACK));
        formatters.put("dark_aqua", in -> in.setColor(ChatColor.DARK_AQUA));
        formatters.put("dark_blue", in -> in.setColor(ChatColor.DARK_BLUE));
        formatters.put("dark_gray", in -> in.setColor(ChatColor.DARK_GRAY));
        formatters.put("dark_green", in -> in.setColor(ChatColor.DARK_GREEN));
        formatters.put("dark_purple", in -> in.setColor(ChatColor.DARK_PURPLE));
        formatters.put("dark_red", in -> in.setColor(ChatColor.DARK_RED));
        formatters.put("gold", in -> in.setColor(ChatColor.GOLD));
        formatters.put("gray", in -> in.setColor(ChatColor.GRAY));
        formatters.put("green", in -> in.setColor(ChatColor.GREEN));
        formatters.put("strikethrough", in -> in.setStrikethrough(true));
        formatters.put("bold", in -> in.setBold(true));
        formatters.put("obfuscate", in -> in.setObfuscated(true));
        formatters.put("underline", in -> in.setUnderlined(true));
        formatters.put("italicize", in -> in.setItalic(true));
    }

    /**
     * Adds an IComponentSettings object to the map.
     * @param name The name of the IComponentSettings object
     * @param componentFormatter The IComponentSettings object itself
     */
    public void addComponent(String name, ComponentSettings componentFormatter) {
        Validate.isTrue(!formatters.containsKey(name), "a component with that name already exists");
        formatters.put(Objects.requireNonNull(name, "name cannot be null"),
                Objects.requireNonNull(componentFormatter, "componentFormatter cannot be null"));
    }

    /**
     * Gets the IComponentSettings object associated with the provided name.
     * @param name The name of the IComponentSettings object
     * @return The associated IComponentSettings, or null if there are none with the specified name
     */
    public ComponentSettings getComponent(String name) {
        return formatters.get(name);
    }

    /**
     * Gets whether or not the specified component exists.
     * @param name The name of the component to search for
     * @return true if the component exists, false otherwise
     */
    public boolean hasComponent(String name) {
        return formatters.containsKey(name);
    }

    /**
     * Gets the names of all the components stored in this TextStylizer instance.
     * @return A List of all the names of the components store in this TextStylizer instance
     */
    public List<String> getComponentNames() {
        return new ArrayList<>(formatters.keySet());
    }

    /**
     * Gets all of the IComponentSettings objects stored in this TextStylizer instance.
     * @return A List of all the IComponentSettings objects stored in this TextStylizer instance
     */
    public List<ComponentSettings> getComponentSettings() {
        return new ArrayList<>(formatters.values());
    }
}