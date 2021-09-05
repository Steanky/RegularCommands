package io.github.regularcommands.commands;

import io.github.regularcommands.completer.ArgumentCompleter;
import io.github.regularcommands.converter.MatchResult;
import io.github.regularcommands.converter.Parameter;

import io.github.regularcommands.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.List;

/**
 * Represents a command, which should conceptually organize a number of related CommandForms. Strictly, RegularCommands
 * have a unique name (which is used to identify it) and a user-friendly usage string.
 */
public abstract class RegularCommand {
    private final String name;
    private final List<CommandForm<?>> forms;
    private final TextComponent.Builder usageBuilder;

    /**
     * Creates a new RegularCommand with the specified name and list of forms.
     * @param name The name of the RegularCommand
     */
    public RegularCommand(String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.forms = new ArrayList<>();
        this.usageBuilder = Component.text()
                .append(Component.text("Usages for /" + name + ": "))
                .append(Component.newline());
    }

    /**
     * Adds a form to this RegularCommand.
     * @param form The form to add
     */
    public void addForm(CommandForm<?> form) {
        forms.add(Objects.requireNonNull(form, "form cannot be null"));

        usageBuilder.append(Component.text('/')).append(Component.text(getName())).append(Component.space());

        for(Parameter parameter : form) {
            usageBuilder.append(Component.text(parameter.getUsage())).append(Component.space());
        }

        Component usage = form.getUsage();
        if(usage != null && usage != Component.empty()) {
            usageBuilder.append(Component.text("— ")).append(usage);
        }

        usageBuilder.append(Component.newline());
    }

    /**
     * Gets the name of this RegularCommand.
     * @return The name of this RegularCommand
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the usage string that contains information about all the forms of this command.
     * @return The usage string for this RegularCommand
     */
    public Component getUsage() { return usageBuilder.build(); }

    /**
     * Returns a list of all CommandForm objects that match the provided argument array.
     * @param args The argument array used to check for matches
     * @param sender The CommandSender that is attempting to run this command
     * @return All matching command forms, or an empty list if none exist
     */
    public List<MatchResult> getMatches(String[] args, CommandSender sender) {
        List<MatchResult> matches = new ArrayList<>();
        for(CommandForm<?> form : forms) {
            //check permissions before running relatively expensive matching algorithm
            if(form.getPermissions().validateFor(sender)) {
                MatchResult matchResult = form.matches(args);

                if(matchResult.matches()) {
                    matches.add(matchResult);
                }
            }
            else {
                matches.add(new MatchResult(form, false, false, null));
            }
        }

        return matches;
    }

    /**
     * Attempts to generate a tab completion list given a CommandManager, CommandSender, and an array of strings
     * corresponding to a partially completed command.
     * @param manager The invoking CommandManager object
     * @param sender The CommandSender that is attempting to tab complete
     * @param args The current argument list, which may be partially or fully completed but should never be null or an
     *             empty array
     * @return A list containing tab completions, or an empty list if none exist
     */
    public List<String> getCompletions(CommandManager manager, CommandSender sender, String[] args) {
        List<String> possibleCompletions = new ArrayList<>();

        for(CommandForm<?> form : forms) {
            if(form.matchScore(args) >= 0) {
                ArgumentCompleter completer = form.getCompleter();

                if(completer != null) {
                    List<String> formCompletions = completer.complete(new Context(manager, form, sender), args);

                    if(formCompletions != null) {
                        possibleCompletions.addAll(formCompletions);
                    }
                }
            }
        }

        return possibleCompletions;
    }
}
