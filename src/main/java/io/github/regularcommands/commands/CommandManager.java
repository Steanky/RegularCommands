package io.github.regularcommands.commands;

import io.github.regularcommands.converter.ConversionResult;
import io.github.regularcommands.converter.MatchResult;
import io.github.regularcommands.message.BasicMessageResources;
import io.github.regularcommands.message.DefaultMessages;
import io.github.regularcommands.message.MessageResources;
import io.github.regularcommands.stylize.ComponentSettings;
import io.github.regularcommands.stylize.TextStylizer;
import io.github.regularcommands.util.ArrayUtils;
import io.github.regularcommands.util.StringUtils;
import io.github.regularcommands.validator.CommandValidator;
import io.github.regularcommands.validator.ValidationResult;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

/**
 * This class keeps track of all registered commands and includes some utility functions.
 */
public class CommandManager implements CommandExecutor, TabCompleter {
    private class SimpleCommand extends RegularCommand {
        private SimpleCommand(String name) {
            super(CommandManager.this, name);
        }
    }

    private final Plugin plugin;
    private final MessageResources messageResources;
    private final Logger logger;
    private final Map<String, RegularCommand> commands;

    private final StringBuilder BUFFER = new StringBuilder(); //used for internal string parsing

    private static final List<String> EMPTY_STRING_LIST = new ArrayList<>();

    /**
     * Creates a new CommandManager and associates it with the specified plugin.
     * @param plugin The associated plugin
     */
    public CommandManager(@NotNull Plugin plugin, @NotNull MessageResources messageResources) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.messageResources = Objects.requireNonNull(messageResources, "messageResources cannot be null");
        logger = plugin.getLogger();
        commands = new HashMap<>();
    }

    /**
     * Registers a RegularCommand with this manager.
     * @param command The RegularCommand to register
     */
    public void registerCommand(@NotNull RegularCommand command) {
        String name = Objects.requireNonNull(command, "command cannot be null").getName();

        if(!commands.containsKey(name)) {
            commands.put(name, command);
            PluginCommand pluginCommand = Objects.requireNonNull(plugin.getServer().getPluginCommand(command.getName()),
                    "command must also be defined in plugin.yml");
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
        else {
            throw new IllegalArgumentException("a command with that name has already been registered");
        }
    }

    /**
     * Returns the RegularCommand with the specified name.
     * @param name The name of the command
     * @return The RegularCommand with the given unique name, or null if it doesn't exist
     */
    public RegularCommand getCommand(@NotNull String name) {
        return commands.get(name);
    }

    /**
     * Returns if a RegularCommand with the given name has been registered.
     * @param name The name of the command
     * @return if it has been registered
     */
    public boolean hasCommand(@NotNull String name) {
        return commands.containsKey(name);
    }

    /**
     * Returns the JavaPlugin this CommandManager instance is attached to.
     * @return The associated JavaPlugin
     */
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the logger used by this instance, which is the same logger that is used by the bound JavaPlugin.
     * @return The associated Logger
     */
    public @NotNull Logger getLogger() { return logger; }

    public @NotNull MessageResources getMessageResources() {
        return messageResources;
    }

    /**
     * Registers a CommandForm with this CommandManager. A default implementation of RegularCommand will be created if
     * one with the given name is absent; if the name exists, the form will be added to the already-present command.
     * Note that it is still necessary to have a command matching the given name defined in plugin.yml.
     * @param name The name of the command to register the form under
     * @param form The CommandForm instance to register
     */
    public void registerForm(@NotNull String name, @NotNull CommandForm<?> form) {
        commands.computeIfAbsent(name, SimpleCommand::new).addForm(form);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        RegularCommand regularCommand = getCommand(command.getName());

        if(regularCommand != null) {
            List<MatchResult> matches = regularCommand.getMatches(parse(args), commandSender); //get all matches

            if(matches.size() > 0) {
                for(MatchResult match : matches) { //loop all matches
                    if(match.hasPermission()) { //check permissions match first
                        ConversionResult<Object[]> conversionResult = match.getConversionResult();

                        if(conversionResult.isValid()) { //conversion was a success
                            CommandForm<?> form = match.getForm();
                            Component output = validateAndExecute(form, commandSender, conversionResult.getConversion());

                            if(output != null) { //we have something to display
                                commandSender.sendMessage(output);
                            }
                        }
                        else { //conversion error
                            commandSender.sendMessage(conversionResult.getErrorMessage());
                        }
                    }
                    else { //sender does not have the required permissions
                        commandSender.sendMessage(messageResources.namedComponent(DefaultMessages.ERROR_NO_PERMISSION));
                    }
                }
            }
            else { //no matching forms
                commandSender.sendMessage(regularCommand.getUsage());
            }
        }
        else {
            getLogger().warning(String.format("CommandSender '%s' tried to execute command '%s', which should not be " +
                    "possible due to it not being present in the command map.", commandSender.getName(),
                    command.getName()));
        }

        return true;
    }

    private <T> Component validateAndExecute(CommandForm<T> form, CommandSender sender, Object[] args) {
        Context context = new Context(this, form, sender);
        CommandValidator<T, ?> validator = form.getValidator(context, args);

        if(validator != null) {
            ValidationResult<T> result = validator.validate(context, args);

            if(result.isValid()) {
                return form.execute(context, args, result.getData());
            }
            else {
                sender.sendMessage(result.getErrorMessage());
            }
        }
        else {
            return form.execute(context, args, null);
        }

        return null;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {
        if(args.length > 0) { //possibly redundant, needs testing
            RegularCommand regularCommand = commands.get(command.getName());

            if(regularCommand != null) {
                return regularCommand.getCompletions(this, commandSender, parse(args));
            }
        }

        return EMPTY_STRING_LIST;
    }

    private String[] parse(String[] args) {
        BUFFER.setLength(0);

        List<String> result = new ArrayList<>();
        boolean quotation = false;
        int lastOpeningQuoteResult = 0;
        int lastOpeningQuoteArg = 0;
        int resultIndex = 0;
        int argIndex = 0;
        for(String arg : args) {
            if(quotation) {
                BUFFER.append(' ');

                if(arg.endsWith("\"")) {
                    quotation = false;
                    BUFFER.append(arg.length() == 1 ? StringUtils.EMPTY : arg.substring(0, arg.length() - 1));
                    result.add(BUFFER.toString());
                    BUFFER.setLength(0);
                    resultIndex++;
                }
                else {
                    BUFFER.append(arg);
                }
            }
            else {
                if(arg.startsWith("\"")) {
                    quotation = true;
                    BUFFER.append(arg.length() == 1 ? StringUtils.EMPTY : arg.substring(1));
                    lastOpeningQuoteResult = resultIndex;
                    lastOpeningQuoteArg = argIndex;
                }
                else {
                    result.add(arg);
                    resultIndex++;
                }
            }
            argIndex++;
        }

        if(BUFFER.length() > 0) {
            if (result.size() > lastOpeningQuoteResult) {
                result.subList(lastOpeningQuoteResult, result.size()).clear();
            }

            result.addAll(Arrays.asList(args).subList(lastOpeningQuoteArg, args.length));
        }

        return result.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }
}