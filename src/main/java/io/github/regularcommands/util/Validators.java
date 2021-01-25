package io.github.regularcommands.util;

import io.github.regularcommands.validator.CommandValidator;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Static utility class. Contains several default CommandValidators that can be used to check against who is running
 * the command (entity, player, console, or block).
 */
public final class Validators {
    public static CommandValidator ENTITY_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof Entity) {
            return ImmutablePair.of(true, null);
        }

        return ImmutablePair.of(false, "Only entities can execute that command.");
    });

    public static CommandValidator PLAYER_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof Player) {
            return ImmutablePair.of(true, null);
        }

        return ImmutablePair.of(false, "Only players can execute that command.");
    });

    public static CommandValidator CONSOLE_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof ConsoleCommandSender) {
            return ImmutablePair.of(true, null);
        }

        return ImmutablePair.of(false, "Only consoles can execute that command.");
    });

    public static CommandValidator BLOCK_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof BlockCommandSender) {
            return ImmutablePair.of(true, null);
        }

        return ImmutablePair.of(false, "Only command blocks can execute that command.");
    });
}
