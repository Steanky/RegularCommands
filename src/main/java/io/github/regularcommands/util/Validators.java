package io.github.regularcommands.util;

import io.github.regularcommands.validator.CommandValidator;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Static utility class for holding builtin CommandValidators along with some factory methods.
 */
public final class Validators {
    public static CommandValidator PLAYER_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof Player) {
            return new ImmutablePair<>(true, null);
        }

        return new ImmutablePair<>(false, "Only players can execute that command.");
    });

    public static CommandValidator CONSOLE_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof ConsoleCommandSender) {
            return new ImmutablePair<>(true, null);
        }

        return new ImmutablePair<>(false, "Only consoles can execute that command.");
    });

    public static CommandValidator BLOCK_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof BlockCommandSender) {
            return new ImmutablePair<>(true, null);
        }

        return new ImmutablePair<>(false, "Only command blocks can execute that command.");
    });
}
