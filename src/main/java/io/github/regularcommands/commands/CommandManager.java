package io.github.regularcommands.commands;

import io.github.regularcommands.converter.ConversionResult;
import io.github.regularcommands.converter.MatchResult;
import io.github.regularcommands.stylize.ComponentSettings;
import io.github.regularcommands.stylize.TextStylizer;
import io.github.regularcommands.util.ArrayUtils;
import io.github.regularcommands.util.StringUtils;
import io.github.regularcommands.validator.CommandValidator;
import io.github.regularcommands.validator.ValidationResult;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

/**
 * This class keeps track of all registered commands and includes some utility functions.
 */
public class CommandManager implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final Map<String, RegularCommand> commands;
    private final TextStylizer stylizer; //used to stylize text

    private final StringBuilder BUFFER = new StringBuilder(); //used for internal string parsing

    private static final List<String> EMPTY_STRING_LIST = new ArrayList<>();

    /**
     * Creates a new CommandManager and associates it with the specified plugin.
     * @param plugin The associated plugin
     */
    public CommandManager(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        logger = plugin.getLogger();
        commands = new HashMap<>();
        stylizer = new TextStylizer();
    }

    /**
     * Registers a RegularCommand with this manager.
     * @param command The RegularCommand to register
     */
    public void registerCommand(RegularCommand command) {
        commands.put(Objects.requireNonNull(command, "command cannot be null").getName(), command);
        PluginCommand pluginCommand = Objects.requireNonNull(plugin.getServer().getPluginCommand(command.getName()),
                "command must also be defined in plugin.yml");
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    /**
     * Returns the JavaPlugin this CommandManager instance is attached to.
     * @return The associated JavaPlugin
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the logger used by this instance, which is the same logger that is used by the bound JavaPlugin.
     * @return The associated Logger
     */
    public Logger getLogger() { return logger; }

    /**
     * Returns the TextStylizer used to stylize command return values
     * @return The TextStylizer used by this instance
     */
    public TextStylizer getStylizer() { return stylizer; }

    /**
     * Sends a player a formatted message, which is stylized according to the same rules as text returned from a
     * CommandForm. This should never be called from an asynchronous context, as it will likely corrupt the internal
     * buffer used for parsing input.
     * @param player The player to send the message to
     * @param message The message to send
     */
    public void sendStylizedMessage(Player player, String message) {
        player.spigot().sendMessage(parseStylizedMessage(message));
    }

    /**
     * Broadcasts the formatted message to the entire server, which will be stylized according to the same rules as text
     * returned from a CommandForm. This should never be called from an asynchronous context, as it will likely corrupt
     * the internal buffer used for parsing input.
     * @param message The message to broadcast
     */
    public void broadcastStylizedMessage(String message) {
        plugin.getServer().spigot().broadcast(parseStylizedMessage(message));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        RegularCommand regularCommand = commands.get(command.getName());

        if(regularCommand != null) {
            List<MatchResult> matches = regularCommand.getMatches(parse(args), commandSender); //get all matches

            if(matches.size() > 0) {
                for(MatchResult match : matches) { //loop all matches
                    if(match.hasPermission()) { //test permissions first
                        ConversionResult<Object[]> conversionResult = match.getConversionResult();

                        if(conversionResult.isValid()) { //conversion was a success
                            CommandForm form = match.getForm();
                            Context context = new Context(this, commandSender);
                            CommandValidator validator = form.getValidator(context, conversionResult.getConversion());
                            ValidationResult validationResult = null;

                            if(validator != null) {
                                validationResult = validator.validate(context, form, conversionResult.getConversion());
                            }

                            if(validator == null || validationResult.isValid()) {
                                String output = form.execute(context, conversionResult.getConversion());

                                if(output != null) { //we have something to display
                                    if(form.canStylize()) { //stylize if we can
                                        //let BadFormatExceptions propagate! they are the fault of the library user
                                        commandSender.spigot().sendMessage(parseStylizedMessage(output));
                                    }
                                    else { //send raw output because this command doesn't support stylization
                                        commandSender.sendMessage(output);
                                    }
                                }
                            }
                            else { //validation error
                                sendErrorMessage(commandSender, validationResult.getErrorMessage());
                            }
                        }
                        else { //conversion error
                            sendErrorMessage(commandSender, conversionResult.getErrorMessage());
                        }
                    }
                    else { //sender does not have the required permissions
                        sendErrorMessage(commandSender, "You do not have permission to execute this command.");
                    }
                }
            }
            else { //no matching commandforms
                commandSender.sendMessage(regularCommand.getUsage());
            }
        }
        else {
            logger.severe(String.format("CommandSender '%s' tried to execute command '%s', which should not be " +
                    "possible due to it not being present in the command map.", commandSender.getName(),
                    command.getName()));

            sendErrorMessage(commandSender, "That command has been registered with this manager, but was unable " +
                    "to be found in the internal mappings. Report this error to your server admins.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
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

    /**
     * Converts an input string into an array of TextComponents using RegularCommand's formatting rules, which are
     * defined as follows:
     *
     * <p>&gt;modifiers{text} unaffected-text &gt;more-modifiers|additional-modifier{more-text}</p>
     *
     * <p>For example, the following would appear as red, underlined text:</p>
     *
     * <p>&gt;red|underlined{This text is red and underlined.}</p>
     *
     * <p>This engine supports all of Minecraft's built-in effects. Custom ones, including embedded links and other
     * advanced features, can be registered via this manager's TextStylizer.</p>
     *
     * <p>If the format is syntactically invalid, a BadFormatException will be thrown. It is recommended to run user input
     * through StringUtils#escapify before passing it through this function, as it will properly escape any special
     * characters.</p>
     * @param input The input text
     * @return An array of TextComponents.
     */
    public TextComponent[] parseStylizedMessage(String input) {
        BUFFER.setLength(0);

        List<TextComponent> components = new ArrayList<>();
        List<ComponentSettings> componentFormatters = new ArrayList<>();

        boolean escape = false;
        boolean name = false;
        boolean component = false;
        int i = 0;
        for(; i < input.length(); i++) {
            char character = input.charAt(i);
            switch (character) {
                case '{':
                    if(!escape) {
                        if(component) {
                            throw new BadFormatException(formatStylizerError("Unescaped curly " +
                                    "bracket (nested groups are not allowed).", input, i));
                        }

                        if(!name) {
                            throw new BadFormatException(formatStylizerError("Format groups " +
                                    "must specify at least one valid formatter name.", input, i));
                        }

                        if(BUFFER.length() > 0) {
                            String formatterName = BUFFER.toString();
                            ComponentSettings formatter = stylizer.getComponent(formatterName);

                            if(formatter != null) {
                                name = false;
                                component = true;

                                componentFormatters.add(formatter);
                                BUFFER.setLength(0);
                            }
                            else {
                                throw new BadFormatException(formatStylizerError("Formatter '" + formatterName +
                                        "' does not exist.", input, i));
                            }
                        }
                        else {
                            throw new BadFormatException(formatStylizerError("Format groups " +
                                    "must specify at least one valid formatter name.", input, i));
                        }
                    }
                    else {
                        BUFFER.append(character);
                        escape = false;
                    }
                    break;
                case '}':
                    if(!escape) {
                        if(name) {
                            throw new BadFormatException(formatStylizerError("Unescaped " +
                                    "close bracket in name specifier.", input, i));
                        }

                        if(!component) {
                            throw new BadFormatException(formatStylizerError("Unescaped close bracket in " +
                                    "non-component region.", input, i));
                        }

                        if(componentFormatters.size() > 0) {
                            TextComponent textComponent = new TextComponent(BUFFER.toString());

                            for(ComponentSettings formatter : componentFormatters) {
                                formatter.apply(textComponent);
                            }

                            components.add(textComponent);
                            componentFormatters.clear();

                            component = false;
                            BUFFER.setLength(0);
                        }
                        else {
                            throw new BadFormatException(formatStylizerError("You must specify at least one " +
                                    "formatter.", input, i));
                        }
                    }
                    else {
                        BUFFER.append(character);
                        escape = false;
                    }
                    break;
                case '>':
                    if(!escape) {
                        if(name) {
                            throw new BadFormatException(formatStylizerError("Unescaped name specifier token " +
                                    "in already present name specifier.", input, i));
                        }

                        if(component) {
                            throw new BadFormatException(formatStylizerError("Unescaped name specifier token " +
                                    "in component (nested components are not allowed).", input, i));
                        }

                        if(BUFFER.length() > 0) {
                            components.add(new TextComponent(BUFFER.toString()));
                            BUFFER.setLength(0);
                        }

                        name = true;
                    }
                    else {
                        BUFFER.append(character);
                        escape = false;
                    }
                    break;
                case '\\':
                    if(!escape) {
                        escape = true;
                    }
                    else {
                        BUFFER.append(character);
                        escape = false;
                    }
                    break;
                case '|':
                    if(!escape) {
                        if(name) {
                            if(BUFFER.length() > 0) {
                                String formatterName = BUFFER.toString();
                                ComponentSettings formatter = stylizer.getComponent(formatterName);

                                if(formatter != null) {
                                    componentFormatters.add(formatter);
                                    BUFFER.setLength(0);
                                }
                                else {
                                    throw new BadFormatException(formatStylizerError("Formatter '" +
                                            formatterName + "' does not exist or has not been registered.", input, i));
                                }
                            }
                            else {
                                throw new BadFormatException(formatStylizerError("Invalid component formatter " +
                                        "name; cannot be an empty string.", input, i));
                            }
                        }
                        else {
                            throw new BadFormatException(formatStylizerError("Unexpected formatter name " +
                                    "delimiter.", input, i));
                        }
                    }
                    else {
                        BUFFER.append(character);
                        escape = false;
                    }
                    break;
                default:
                    BUFFER.append(character);
                    escape = false;
                    break;
            }
            i++;
        }

        if(name) {
            throw new BadFormatException(formatStylizerError("Unfinished format name specifier.", input, i));
        }

        if(component) {
            throw new BadFormatException(formatStylizerError("Unfinished text component.", input, i));
        }

        if(BUFFER.length() > 0) {
            components.add(new TextComponent(BUFFER.toString()));
        }

        return components.toArray(ArrayUtils.EMPTY_TEXT_COMPONENT_ARRAY);
    }

    private String formatStylizerError(String message, String inputString, int currentIndex) {
        if(message == null || message.length() == 0) {
            return "Stylization error: empty or null message";
        }

        return "Stylization error: " + message + " ~@['" + inputString.substring(Math.max(currentIndex - 10, 0),
                Math.min(currentIndex + 10, inputString.length())) + "'], string index " + currentIndex + ".";
    }

    private void sendErrorMessage(CommandSender sender, String text) {
        TextComponent component = new TextComponent(text);
        component.setColor(ChatColor.RED);
        sender.spigot().sendMessage(component);
    }
}