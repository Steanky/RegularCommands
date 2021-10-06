package io.github.zap.regularcommands.commands;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds information about the permissions required to run a command.
 */
public class PermissionData {
    private final Set<Permission> permissions;
    private final boolean requiresOp;

    /**
     * Creates a new PermissionData object with the specified permissions set and operator potentially required.
     * @param permissions The set of required permissions
     * @param requiresOp Whether or not operator should be required
     */
    public PermissionData(@NotNull Set<Permission> permissions, boolean requiresOp) {
        this.permissions = new HashSet<>(permissions);
        this.requiresOp = requiresOp;
    }

    /**
     * Creates a new PermissionData object with the specified permissions set, without requiring operator.
     * @param permissions The set of required permissions
     */
    public PermissionData(@NotNull Set<Permission> permissions) {
        this(permissions, false);
    }

    /**
     * Creates a new PermissionData object with no required permissions and operator potentially required.
     * @param requiresOp Whether or not operator should be required
     */
    public PermissionData(boolean requiresOp) {
        this(new HashSet<>(), requiresOp);
    }

    /**
     * Creates a new PermissionData object with no required permissions or operator.
     */
    public PermissionData() {
        this(new HashSet<>(), false);
    }

    /**
     * Tests if the provided Permissible has all the permissions specified by this PermissionData object.
     * @param permissible The Permissible to test for
     * @return True if the Permissible has, at minimum, all the required permissions. False otherwise
     */
    public boolean validateFor(@NotNull Permissible permissible) {
        return (!requiresOp || permissible.isOp()) && permissions.stream().allMatch(permissible::hasPermission);
    }
}
