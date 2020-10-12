package io.github.regularcommands.provider;

import io.github.regularcommands.adapter.AdapterManager;

public interface AdapterManagerProvider {
    /**
     * Gets a descriptive, user-friendly name for the current context. This should be a static string.
     * @return The name of the current context
     */
    String getContextName();

    /**
     * Gets the AdapterManager used to interface with this object.
     * @return The AdapterManager used to interface with this object
     */
    AdapterManager getAdapterManager();
}
