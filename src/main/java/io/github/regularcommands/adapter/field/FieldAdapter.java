package io.github.regularcommands.adapter.field;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A class used to generalize field access. This is the equivalent of GroupAdapter, but is used for modifying single
 * objects instead of collections.
 */
public class FieldAdapter {
    private final String id;
    private final Type type;
    private final FieldSetter setter;
    private final FieldGetter getter;

    /**
     * Creates an object that can be used to read and write to a field.
     * @param id The name of the adapter
     * @param type The type of the field
     * @param getter The IFieldGetter used to retrieve the value of the field
     * @param setter The IFieldSetter used to set the value of the field
     */
    public FieldAdapter(String id, Type type, FieldGetter getter, FieldSetter setter) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.getter = Objects.requireNonNull(getter, "getter cannot be null");
        this.setter = Objects.requireNonNull(setter, "setter cannot be null");
    }

    /**
     * Gets the name of the FieldAdapter, used by the AdapterManager as a key.
     * @return The ID associated with this FieldAdapter instance
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the value of the field.
     * @return The value of the field
     */
    public Object get() {
        return getter.getField();
    }

    /**
     * Sets the value of the field.
     * @param value The new value of the field
     */
    public void set(Object value) {
        setter.setField(value);
    }

    /**
     * Returns the type of the field this FieldAdapter is attached to.
     * @return A Type object corresponding to the type of the associated field
     */
    public Type getType() { return type; }
}
