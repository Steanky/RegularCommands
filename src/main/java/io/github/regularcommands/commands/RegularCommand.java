package io.github.regularcommands.commands;

import io.github.regularcommands.completer.ArgumentCompleter;
import io.github.regularcommands.converter.MatchResult;
import io.github.regularcommands.converter.Parameter;

import org.bukkit.command.CommandSender;

import java.util.*;

public abstract class RegularCommand {
    private final String name;
    private final List<CommandForm> forms;
    private final StringBuilder usageBuilder;

    /**
     * Creates a new RegularCommand with the specified name and list of forms.
     * @param name The name of the RegularCommand
     */
    public RegularCommand(String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.forms = new ArrayList<>();
        this.usageBuilder = new StringBuilder("Usages for /" + name + ": " + '\n');
    }

    /**
     * Adds a form to this RegularCommand.
     * @param form The form to add
     */
    public void addForm(CommandForm form) {
        forms.add(Objects.requireNonNull(form, "form cannot be null"));

        usageBuilder.append('/').append(getName()).append(' ');

        for(Parameter parameter : form) {
            usageBuilder.append(parameter.getUsage()).append(' ');
        }

        String usage = form.getUsage();
        if(usage != null && !usage.equals("")) {
            usageBuilder.append("— ").append(usage);
        }

        usageBuilder.append('\n');
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
    public String getUsage() { return usageBuilder.toString(); }

    /**
     * Returns a list of all CommandForm objects that match the provided argument array.
     * @param args The argument array used to check for matches
     * @param sender The CommandSender that is attempting to run this command
     * @return All matching command forms, or an empty list if none exist
     */
    protected List<MatchResult> getMatches(String[] args, CommandSender sender) {
        List<MatchResult> matches = new ArrayList<>();
        for(CommandForm form : forms) {
            if(form.getPermissions().validateFor(sender)) { //check permissions before running relatively expensive matching algorithm
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
     * @return A list containing tab completions, or null if none are found
     */
    protected List<String> getCompletions(CommandManager manager, CommandSender sender, String[] args) {
        List<String> possibleCompletions = new ArrayList<>();

        for(CommandForm form : forms) {
            if(form.fuzzyMatch(args) > 0) {
                ArgumentCompleter completer = form.getCompleter();

                if(completer != null) {
                    List<String> formCompletions = completer.complete(new Context(manager, sender), form, args);

                    if(formCompletions != null) {
                        possibleCompletions.addAll(formCompletions);
                    }
                }
            }
        }

        return possibleCompletions.size() == 0 ? null : possibleCompletions;
    }
}