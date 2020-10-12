package io.github.regularcommands.provider;

public interface AdapterGroupChild {
    /**
     * Gets the 'parent' IAdapterManagerProvider of this object. The object this returns should generally contain the
     * object that is implementing this interface in some sort of collection.
     * @return The parent IAdapterManagerProvider
     */
    AdapterManagerProvider getParent();
}
