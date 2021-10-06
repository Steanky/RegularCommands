package io.github.zap.regularcommands.commands;

import io.github.zap.regularcommands.completer.ArgumentCompleter;
import io.github.zap.regularcommands.converter.MatchResult;
import io.github.zap.regularcommands.converter.Parameter;

import io.github.zap.regularcommands.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

/**
 * Represents a command, which should conceptually organize a number of related CommandForms. Strictly, RegularCommands
 * have a unique name (which is used to identify it) and a user-friendly usage string.
 */
public abstract class RegularCommand {
    private final CommandManager manager;
    private final String name;
    private final List<CommandForm<?>> forms;
    private final StringBuilder usageBuilder;

    /**
     * Creates a new RegularCommand with the specified name and list of forms.
     * @param name The name of the RegularCommand
     */
    public RegularCommand(@NotNull CommandManager manager, @NotNull String name) {
        this.manager = manager;
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.forms = new ArrayList<>();
        this.usageBuilder = new StringBuilder("Usages for /" + name + ": \n");
    }

    /**
     * Adds a form to this RegularCommand.
     * @param form The form to add
     */
    public void addForm(@NotNull CommandForm<?> form) {
        forms.add(Objects.requireNonNull(form, "form cannot be null"));

        usageBuilder.append('/').append(getName()).append(' ');

        for(Parameter parameter : form) {
            usageBuilder.append(parameter.getUsage()).append(' ');
        }

        String usage = form.getUsage();
        if(usage != null && !usage.equals(StringUtils.EMPTY)) {
            usageBuilder.append("— ").append(usage);
        }

        usageBuilder.append('\n');
    }

    /**
     * Gets the manager associated with this RegularCommand
     * @return The manager associated with this command
     */
    public @NotNull CommandManager getManager() {
        return manager;
    }

    /**
     * Gets the name of this RegularCommand.
     * @return The name of this RegularCommand
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the usage string that contains information about all the forms of this command.
     * @return The usage string for this RegularCommand
     */
    public @NotNull String getUsage() { return usageBuilder.toString(); }

    /**
     * Returns a list of all CommandForm objects that match the provided argument array.
     * @param args The argument array used to check for matches
     * @param sender The CommandSender that is attempting to run this command
     * @return All matching command forms, or an empty list if none exist
     */
    public @NotNull List<MatchResult> getMatches(@NotNull String[] args, @NotNull CommandSender sender) {
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
     * Attempts to generate a tab completion list given a CommandSender and an array of strings corresponding to a
     * partially completed command.
     * @param sender The CommandSender that is attempting to tab complete
     * @param args The current argument list, which may be partially or fully completed but should never be null or an
     *             empty array
     * @return A list containing tab completions, or an empty list if none exist
     */
    public @NotNull List<String> getCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> possibleCompletions = new ArrayList<>();

        for(CommandForm<?> form : forms) {
            if(form.matchScore(args) >= 0) {
                ArgumentCompleter completer = form.getCompleter();

                if(completer != null) {
                    List<String> formCompletions = completer.complete(new Context(form, sender), args);

                    if(formCompletions != null) {
                        possibleCompletions.addAll(formCompletions);
                    }
                }
            }
        }

        return possibleCompletions;
    }
}