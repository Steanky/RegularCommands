package io.github.regularcommands.adapter;

import io.github.regularcommands.adapter.field.FieldAdapter;
import io.github.regularcommands.adapter.group.GroupAdapter;

import java.util.*;

/**
 * Used by IAdapterManagerProviders to expose a way to access its objects.
 */
public class AdapterManager {
    private final Map<String, GroupAdapter> groupAdapters;
    private final Map<String, FieldAdapter> fieldAdapters;

    /**
     * Creates a new, immutable AdapterManager object.
     */
    public AdapterManager() {
        groupAdapters = new HashMap<>();
        fieldAdapters = new HashMap<>();
    }

    /**
     * Returns whether or not the specified group adapter exists.
     * @param id The adapter id to test for
     * @return true if there is an adapter associated with id, false otherwise
     */
    public boolean hasGroupAdapter(String id) { return groupAdapters.containsKey(id); }

    /**
     * Returns an adapter that can be used to access a collection or dictionary of objects in a standardized way.
     * @param id The id of the stored group adapter
     * @return The group adapter associated with id, or null if there is none
     */
    public GroupAdapter getGroupAdapter(String id) {
        return groupAdapters.get(id);
    }

    /**
     * Add an adapter to the internal hashtable. Adapters with conflicting names will overwrite their previous
     * version.
     * @param adapter The group adapter to register
     */
    public void registerGroupAdapter(GroupAdapter adapter) {
        groupAdapters.put(adapter.getId(), adapter);
    }

    /**
     * Returns a list of valid GroupAdapter names.
     * @return A list of valid GroupAdapter names
     */
    public List<String> getGroupNames() {
        return new ArrayList<>(groupAdapters.keySet());
    }

    /**
     * Gets the FieldAdapter with the provided name, or null if none exist.
     * @param id The id of the desired FieldAdapter
     * @return The field adapter associated with the provided id
     */
    public FieldAdapter getFieldAdapter(String id) { return fieldAdapters.get(id); }

    /**
     * Adds a new field adapter with the specified name. Adapters with conflicting names will overwrite their previous
     * version.
     * @param adapter The adapter to register
     */
    public void registerFieldAdapter(FieldAdapter adapter) { fieldAdapters.put(adapter.getId(), adapter); }

    /**
     * Returns a list of FieldAdapter names.
     * @return A list of valid FieldAdapter names
     */
    public List<String> getFieldNames() {
        return new ArrayList<>(fieldAdapters.keySet());
    }
}