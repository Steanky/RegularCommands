package io.github.regularcommands.util;

import io.github.regularcommands.validator.CommandValidator;
import io.github.regularcommands.validator.ValidationResult;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Static utility class. Contains several default CommandValidators that can be used to check against who is running
 * the command (entity, player, console, or block).
 */
public final class Validators {
    public static CommandValidator ENTITY_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof Entity) {
            return ValidationResult.of(true, null);
        }

        return ValidationResult.of(false, "Only entities can execute that command.");
    });

    public static CommandValidator PLAYER_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof Player) {
            return ValidationResult.of(true, null);
        }

        return ValidationResult.of(false, "Only players can execute that command.");
    });

    public static CommandValidator CONSOLE_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof ConsoleCommandSender) {
            return ValidationResult.of(true, null);
        }

        return ValidationResult.of(false, "Only consoles can execute that command.");
    });

    public static CommandValidator BLOCK_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof BlockCommandSender) {
            return ValidationResult.of(true, null);
        }

        return ValidationResult.of(false, "Only command blocks can execute that command.");
    });
}
