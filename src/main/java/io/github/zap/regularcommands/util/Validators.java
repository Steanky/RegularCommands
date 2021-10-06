package io.github.zap.regularcommands.util;

import io.github.zap.regularcommands.commands.DefaultKeys;
import io.github.zap.regularcommands.validator.CommandValidator;
import io.github.zap.regularcommands.validator.ValidationResult;
import net.kyori.adventure.text.Component;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Static utility class. Contains several default CommandValidators that can be used to check against who is running
 * the command (entity, player, console, or block).
 */
public final class Validators {
    public static final CommandValidator<CommandSender, ?> ANY = new CommandValidator<>(
            (context, arguments, previousData) ->
                    ValidationResult.of(true, null, context.getSender()));

    public static final CommandValidator<Entity, ?> ENTITY_EXECUTOR = new CommandValidator<>(
            (context, form, arguments) -> {
        CommandSender sender = context.getSender();
        if(sender instanceof Entity) {
            return ValidationResult.of(true, null, (Entity)sender);
        }

        return ValidationResult.of(false, Component.translatable(DefaultKeys.ERROR_ENTITY_EXECUTOR.key()), null);
    });

    public static final CommandValidator<Player, ?> PLAYER_EXECUTOR = new CommandValidator<>(
            (context, form, arguments) -> {
        CommandSender sender = context.getSender();
        if(context.getSender() instanceof Player) {
            return ValidationResult.of(true, null, (Player)sender);
        }

        return ValidationResult.of(false, Component.translatable(DefaultKeys.ERROR_PLAYER_EXECUTOR.key()), null);
    });

    public static final CommandValidator<ConsoleCommandSender, ?> CONSOLE_EXECUTOR = new CommandValidator<>(
            (context, form, arguments) -> {
        CommandSender sender = context.getSender();
        if(sender instanceof ConsoleCommandSender) {
            return ValidationResult.of(true, null, (ConsoleCommandSender)sender);
        }

        return ValidationResult.of(false, Component.translatable(DefaultKeys.ERROR_CONSOLE_EXECUTOR.key()), null);
    });

    public static final CommandValidator<BlockCommandSender, ?> BLOCK_EXECUTOR = new CommandValidator<>(
            (context, form, arguments) -> {
        CommandSender sender = context.getSender();
        if(sender instanceof BlockCommandSender) {
            return ValidationResult.of(true, null, (BlockCommandSender)sender);
        }

        return ValidationResult.of(false, Component.translatable(DefaultKeys.ERROR_BLOCK_EXECUTOR.key()), null);
    });
}
