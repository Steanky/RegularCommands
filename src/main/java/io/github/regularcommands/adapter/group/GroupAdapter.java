package io.github.regularcommands.adapter.group;

import java.util.List;
import java.util.Objects;

/**
 * Encapsulates a container of items such that any specific item can be accessed by providing an appropriate value.
 * Implementations choose which object type is used to access the backing container. For example, an implementation
 * using a backing List would want to use an Integer as a key.
 */
@SuppressWarnings("rawtypes")
public abstract class GroupAdapter {
    private final String id;
    protected final Class keyClass;
    protected final Class valueClass;

    /**
     * Creates a new GroupAdapter using the specified ID.
     * @param id The ID used to identify this instance
     * @param keyClass The type of object used to store objects in the backing group, or null if there are none
     * @param valueClass The type of object contained in the backing group
     */
    public GroupAdapter(String id, Class keyClass, Class valueClass) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.keyClass = keyClass;
        this.valueClass = Objects.requireNonNull(valueClass, "valueClass cannot be null");
    }

    /**
     * Gets the name of the GroupAdapter, used by the AdapterManager as a key.
     * @return The ID associated with this GroupAdapter instance
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the type of object used to store values in the collection. This should be null for implementations
     * that don't require such a value (such as Lists).
     * @return The class of object used to store values in the backing collection, or null if there are none
     */
    public Class getKeyClass() { return keyClass; }

    /**
     * Returns the type of object held by the collection that this GroupAdapter is attached to.
     * @return A Class object corresponding to the type of the associated field
     */
    public Class getValueClass() {
        return valueClass;
    }

    /**
     * Returns whether or not the associated collection or dictionary contains the specified object. Implementations
     * should not throw an exception when Object does not match the expected type; rather, they should simply return
     * false.
     * @param object The object to search the collection for
     * @return true if the object exists, false otherwise
     */
    public abstract boolean contains(Object object);

    /**
     * Deletes an item from the collection, using a key. Implementations should throw an exception if the wrong type
     * is used or if the key does not exist.
     * @param key The key used to locate the item for deletion
     */
    public abstract void deleteItem(Object key);

    /**
     * Returns whether or not the specified key can be used to access the backing collection or dictionary.
     * Implementations should not throw an exception when the Object does not match the expected type; rather, they
     * should simply return false.
     * @param key The object to validate
     * @return true if the key can be used to successfully access an element, false otherwise
     */
    public abstract boolean containsKey(Object key);

    /**
     * Retrieves an item from the backing collection or dictionary. Implementations should throw an exception if the
     * wrong type is used or the key does not exist.
     * @param key The key used to locate the item for retrieval
     * @return The item associated with the provided key
     */
    public abstract Object getItem(Object key);

    /**
     * Accesses the backing collection or dictionary. To prevent improper modification, implementations should create a
     * copy of the original list and return that instead.
     * @return A copy of the backing list
     */
    public abstract List getItems();

    /**
     * Obtains the size of the backing collection.
     * @return The size of the backing collection
     */
    public abstract int size();

    /**
     * Returns a list of objects that can be used to successfully access the backing collection or dictionary.
     * @return A list of objects that can access the backing collection or dictionary
     */
    public abstract List getKeys();

    /**
     * Adds an item to the collection.
     * @param key The key used to add the item
     * @param item The object to add
     * @return Whether the addition was successful
     */
    public abstract boolean addItem(Object key, Object item);

    /**
     * Used to safely access objects stored in the provided GroupAdapter. Will return null if the containsKey()
     * implementation of the groupAdapter returns false when called with the provided key.
     * @param adapter The adapter to access from
     * @param key The key used to access the backing collection via the adapter
     * @return The object associated with the provided key, or null if containsKey() returns false
     */
    public static Object tryGet(GroupAdapter adapter, Object key) {
        if(adapter.containsKey(key)) {
            return adapter.getItem(key);
        }

        return null;
    }
}