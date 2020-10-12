package io.github.regularcommands.adapter.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This implementation of GroupAdapter encapsulates any kind of List.
 */
@SuppressWarnings("rawtypes")
public class ListAdapter extends GroupAdapter {
    private final List list;

    public ListAdapter(String name, Class valueClass, List list) {
        super(name, null, valueClass);
        this.list = Objects.requireNonNull(list, "the backing list cannot be null");
    }

    @Override
    public boolean contains(Object object) {
        return list.contains(object);
    }

    @Override
    public boolean containsKey(Object key) {
        if(key instanceof Integer) {
            int keyInt = (int)key;
            return keyInt >= 0 && keyInt < list.size();
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addItem(Object key, Object item) {
        if(key == null && (item == null ? null : item.getClass()) == super.getValueClass()) {
            list.add(item);
            return true;
        }

        return false;
    }

    @Override
    public Object getItem(Object key) {
        return list.get((int)key);
    }

    @Override
    public void deleteItem(Object key) {
        list.remove((int)key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List getItems() {
        return new ArrayList(list);
    }

    @Override
    public int size() {
        return list.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List getKeys() {
        List list = new ArrayList();
        list.addAll(0, this.list);
        return list;
    }
}