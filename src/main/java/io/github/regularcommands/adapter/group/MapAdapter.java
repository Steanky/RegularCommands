package io.github.regularcommands.adapter.group;

import java.util.*;

/**
 * This implementation of GroupAdapter encapsulates any kind of Map.
 */
@SuppressWarnings("rawtypes")
public class MapAdapter extends GroupAdapter {
    private final Map providerMap;

    public MapAdapter(String name, Class keyClass, Class valueClass, Map providerMap) {
        super(name, keyClass, valueClass);
        this.providerMap = Objects.requireNonNull(providerMap, "the backing Map cannot be null");
    }

    @Override
    public int size() {
        return providerMap.size();
    }

    @Override
    public boolean contains(Object object) {
        return providerMap.containsValue(object);
    }

    @Override
    public boolean containsKey(Object key) { return providerMap.containsKey(key); }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addItem(Object key, Object item) {
        if((key == null ? null : key.getClass()) == keyClass && (item == null ? null : item.getClass()) == valueClass) {
            providerMap.put(key, item);
            return true;
        }

        return false;
    }

    @Override
    public Object getItem(Object key) {
        return providerMap.get(key);
    }

    @Override
    public void deleteItem(Object key) {
        providerMap.remove(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List getItems() {
        return new ArrayList(providerMap.values());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List getKeys() { return new ArrayList(providerMap.keySet()); }
}