package io.github.regularcommands.stylize;

import java.util.*;

/**
 * Data container object, used to store IComponentSettings objects.
 */
public class TextStylizer {
    private final Map<String, ComponentSettings> formatters;

    /**
     * Creates a new TextStylizer object.
     */
    public TextStylizer() {
        formatters = new HashMap<>();
    }

    /**
     * Adds an IComponentSettings object to the map.
     * @param name The name of the IComponentSettings object
     * @param componentFormatter The IComponentSettings object itself
     */
    public final void addComponent(String name, ComponentSettings componentFormatter) {
        formatters.put(Objects.requireNonNull(name, "name cannot be null"),
                Objects.requireNonNull(componentFormatter, "componentFormatter cannot be null"));
    }

    /**
     * Gets the IComponentSettings object associated with the provided name.
     * @param name The name of the IComponentSettings object
     * @return The associated IComponentSettings, or null if there are none with the specified name
     */
    public final ComponentSettings getComponent(String name) {
        return formatters.get(name);
    }

    /**
     * Gets whether or not the specified component exists.
     * @param name The name of the component to search for
     * @return true if the component exists, false otherwise
     */
    public final boolean hasComponent(String name) {
        return formatters.containsKey(name);
    }

    /**
     * Gets the names of all the components stored in this TextStylizer instance.
     * @return A List of all the names of the components store in this TextStylizer instance
     */
    public final List<String> getComponentNames() {
        return new ArrayList<>(formatters.keySet());
    }

    /**
     * Gets all of the IComponentSettings objects stored in this TextStylizer instance.
     * @return A List of all the IComponentSettings objects stored in this TextStylizer instance
     */
    public final List<ComponentSettings> getComponentSettings() {
        return new ArrayList<>(formatters.values());
    }
}