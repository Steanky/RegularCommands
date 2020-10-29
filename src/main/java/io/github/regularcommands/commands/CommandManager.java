package io.github.regularcommands.commands;

import io.github.regularcommands.converter.MatchResult;
import io.github.regularcommands.stylize.ComponentSettings;
import io.github.regularcommands.stylize.TextStylizer;
import io.github.regularcommands.validator.CommandValidator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final Map<String, RegularCommand> commands;

    private final StringBuilder BUFFER = new StringBuilder(); //used for internal string parsing

    private static final List<String> EMPTY_STRING_LIST = new ArrayList<>();
    private static final TextComponent[] EMPTY_TEXT_COMPONENT_ARRAY = new TextComponent[0];

    /**
     * Creates a new CommandManager and associates it with the specified plugin.
     * @param plugin The attached plugin
     */
    public CommandManager(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        logger = plugin.getLogger();
        commands = new HashMap<>();
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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        RegularCommand regularCommand = commands.get(command.getName());

        if(regularCommand != null) {
            List<MatchResult> matches = regularCommand.getMatches(parse(args), commandSender); //get all matches

            if(matches.size() > 0) {
                for(MatchResult match : matches) { //loop all matches
                    if(match.hasPermission()) { //test permissions first
                        ImmutableTriple<Boolean, Object[], String> conversionResult = match.getConversionResult();

                        if(conversionResult.left) { //conversion was a success
                            CommandForm form = match.getForm();
                            Context context = new Context(this, commandSender);
                            CommandValidator validator = form.getValidator(context, conversionResult.middle);
                            ImmutablePair<Boolean, String> validationResult = null;

                            if(validator != null) {
                                validationResult = validator.validate(context, conversionResult.middle);
                            }

                            if(validator == null || validationResult.left) {
                                String output = form.execute(context, conversionResult.middle);

                                if(output != null) { //we have something to display
                                    if(form.canStylize()) { //stylize if we can
                                        ImmutableTriple<Boolean, TextComponent[], String> stylizationResult =
                                                stylize(output);

                                        if(stylizationResult.left) { //send stylized output
                                            commandSender.spigot().sendMessage(stylizationResult.middle);
                                        }
                                        else { //send stylization error message
                                            logger.warning(String.format("A stylizer error occurred when '%s' " +
                                                            "executed command '%s': %s", commandSender.getName(),
                                                    command.getName(), stylizationResult.right));

                                            sendErrorMessage(commandSender, stylizationResult.right);
                                        }
                                    }
                                    else { //send raw output because this command doesn't support stylization
                                        commandSender.sendMessage(output);
                                    }
                                }
                            }
                            else { //validation error
                                sendErrorMessage(commandSender, validationResult.right);
                            }
                        }
                        else { //conversion error
                            sendErrorMessage(commandSender, conversionResult.right);
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
        if(args.length > 0) {
            RegularCommand regularCommand = commands.get(command.getName());

            if(regularCommand != null) {
                List<String> completions = regularCommand.getCompletions(this, commandSender, parse(args));
                return completions == null ? EMPTY_STRING_LIST : completions;
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

    private ImmutableTriple<Boolean, TextComponent[], String> stylize(String input) {
        BUFFER.setLength(0);

        List<TextComponent> components = new ArrayList<>();
        List<ComponentSettings> componentFormatters = new ArrayList<>();

        boolean escape = false;
        boolean name = false;
        boolean component = false;
        int index = 0;
        for(char character : input.toCharArray()) {
            switch (character) {
                case '{':
                    if(!escape) {
                        if(component) {
                            return ImmutableTriple.of(false, null, formatStylizerError("Unescaped curly " +
                                    "bracket (nested groups are not allowed).", input, index));
                        }

                        if(!name) {
                            return ImmutableTriple.of(false, null, formatStylizerError("Format groups " +
                                    "must specify at least one valid formatter name.", input, index));
                        }

                        if(BUFFER.length() > 0) {
                            String formatterName = BUFFER.toString();
                            ComponentSettings formatter = TextStylizer.getInstance().getComponent(formatterName);

                            if(formatter != null) {
                                name = false;
                                component = true;

                                componentFormatters.add(formatter);
                                BUFFER.setLength(0);
                            }
                            else {
                                return ImmutableTriple.of(false, null, formatStylizerError("Formatter '" +
                                        formatterName + "' does not exist.", input, index));
                            }
                        }
                        else {
                            return ImmutableTriple.of(false, null, formatStylizerError("Format groups " +
                                    "must specify at least one valid formatter name.", input, index));
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
                            return ImmutableTriple.of(false, null, formatStylizerError("Unescaped " +
                                    "close bracket in name specifier.", input, index));
                        }

                        if(!component) {
                            return ImmutableTriple.of(false, null, formatStylizerError("Unescaped " +
                                    "close bracket in non-component region.", input, index));
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
                            return ImmutableTriple.of(false, null, formatStylizerError("You must " +
                                    "specify at least one formatter.", input, index));
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
                            return ImmutableTriple.of(false, null, formatStylizerError("Unescaped " +
                                    "name specifier token in name specifier.", input, index));
                        }

                        if(component) {
                            return ImmutableTriple.of(false, null, formatStylizerError("Unescaped " +
                                    "name specifier token in component (nested components are not allowed).", input, index));
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
                                ComponentSettings formatter = TextStylizer.getInstance().getComponent(formatterName);

                                if(formatter != null) {
                                    componentFormatters.add(formatter);
                                    BUFFER.setLength(0);
                                }
                                else {
                                    return ImmutableTriple.of(false, null, formatStylizerError("Formatter" +
                                            " '" + formatterName + "' does not exist.", input, index));
                                }
                            }
                            else {
                                return ImmutableTriple.of(false, null, formatStylizerError("Invalid " +
                                        "component formatter name; cannot be an empty string.", input, index));
                            }
                        }
                        else {
                            return ImmutableTriple.of(false, null, formatStylizerError("Unexpected " +
                                    "formatter name delimiter.", input, index));
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
            index++;
        }

        if(name) {
            return ImmutableTriple.of(false, null, formatStylizerError("Unfinished format name specifier.",
                    input, index));
        }

        if(component) {
            return ImmutableTriple.of(false, null, formatStylizerError("Unfinished text component.", input,
                    index));
        }

        if(BUFFER.length() > 0) {
            components.add(new TextComponent(BUFFER.toString()));
        }

        return ImmutableTriple.of(true, components.toArray(EMPTY_TEXT_COMPONENT_ARRAY), null);
    }

    private String formatStylizerError(String message, String inputString, int currentIndex) {
        return message + " @['" + inputString.substring(Math.max(currentIndex - 10, Math.min(currentIndex + 10,
                inputString.length()))) + "'], string index " + currentIndex + ".";
    }

    private void sendErrorMessage(CommandSender sender, String text) {
        TextComponent component = new TextComponent(text);
        component.setColor(ChatColor.RED);
        sender.spigot().sendMessage(component);
    }
}