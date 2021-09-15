package io.github.regularcommands.util;

import io.github.regularcommands.message.DefaultMessages;
import io.github.regularcommands.validator.CommandValidator;
import io.github.regularcommands.validator.ValidationResult;
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
    public static CommandValidator<Entity, ?> ENTITY_EXECUTOR = new CommandValidator<>((context, form, arguments) -> {
        CommandSender sender = context.getSender();
        if(sender instanceof Entity) {
            return ValidationResult.of(true, null, (Entity)sender);
        }

        return ValidationResult.of(false, context.getManager().getMessageResources()
                .namedComponent(DefaultMessages.ERROR_ENTITY_EXECUTOR), null);
    });

    public static CommandValidator<Player, ?> PLAYER_EXECUTOR = new CommandValidator<>((context, form, arguments) -> {
        CommandSender sender = context.getSender();
        if(context.getSender() instanceof Player) {
            return ValidationResult.of(true, null, (Player)sender);
        }

        return ValidationResult.of(false, context.getManager().getMessageResources()
                .namedComponent(DefaultMessages.ERROR_PLAYER_EXECUTOR), null);
    });

    public static CommandValidator<ConsoleCommandSender, ?> CONSOLE_EXECUTOR = new CommandValidator<>((context, form, arguments) -> {
        CommandSender sender = context.getSender();
        if(sender instanceof ConsoleCommandSender) {
            return ValidationResult.of(true, null, (ConsoleCommandSender)sender);
        }

        return ValidationResult.of(false, context.getManager().getMessageResources()
                .namedComponent(DefaultMessages.ERROR_CONSOLE_EXECUTOR), null);
    });

    public static CommandValidator<BlockCommandSender, ?> BLOCK_EXECUTOR = new CommandValidator<>((context, form, arguments) -> {
        CommandSender sender = context.getSender();
        if(sender instanceof BlockCommandSender) {
            return ValidationResult.of(true, null, (BlockCommandSender)sender);
        }

        return ValidationResult.of(false, context.getManager().getMessageResources()
                .namedComponent(DefaultMessages.ERROR_BLOCK_EXECUTOR), null);
    });
}
