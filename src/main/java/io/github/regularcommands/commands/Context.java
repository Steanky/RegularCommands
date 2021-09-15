package io.github.regularcommands.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An immutable object used as a simple data container. It holds a CommandManager and a CommandSender object.
 */
public class Context {
    private final CommandManager manager;
    private final CommandSender sender;
    private final CommandForm<?> form;

    /**
     * Creates a new Context object, which contains a CommandManager and a CommandSender.
     * @param manager The CommandManager
     * @param sender The CommandSender
     */
    public Context(@NotNull CommandManager manager, @NotNull CommandForm<?> form, @NotNull  CommandSender sender) {
        this.manager = Objects.requireNonNull(manager, "manager cannot be null");
        this.sender = Objects.requireNonNull(sender, "sender cannot be null");
        this.form = Objects.requireNonNull(form, "form cannot be null");
    }

    /**
     * Gets the manager object.
     * @return The manager stored in this Context object
     */
    public @NotNull CommandManager getManager() { return manager; }

    /**
     * Gets the CommandSender object.
     * @return The CommandSender stored in this Context object
     */
    public @NotNull CommandSender getSender() {
        return sender;
    }

    public @NotNull CommandForm<?> getForm() {
        return form;
    }
}