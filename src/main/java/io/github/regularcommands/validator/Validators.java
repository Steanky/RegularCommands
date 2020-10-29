package io.github.regularcommands.validator;

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
    }, false);

    public static CommandValidator CONSOLE_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof ConsoleCommandSender) {
            return new ImmutablePair<>(true, null);
        }

        return new ImmutablePair<>(false, "Only consoles can execute that command.");
    }, false);

    public static CommandValidator BLOCK_EXECUTOR = new CommandValidator((context, arguments) -> {
        if(context.getSender() instanceof BlockCommandSender) {
            return new ImmutablePair<>(true, null);
        }

        return new ImmutablePair<>(false, "Only command blocks can execute that command.");
    }, false);

    /**
     * Creates a new validator that performs a range check on all of the arguments located at the provided indices.
     * The arguments must be comparable.
     * @param range The Range object used to validate
     * @param checkIndices The indices to validate against. All indices must be within range
     * @return A new instance of CommandValidator acting on a range and an array of indices
     */
    public static CommandValidator newRangeValidator(Range<Comparable<?>> range, int... checkIndices) {
        Objects.requireNonNull(range, "range cannot be null");
        Objects.requireNonNull(checkIndices, "checkIndices cannot be null");
        Validate.isTrue(checkIndices.length > 0);

        return new CommandValidator((context, arguments) -> {
            for(int index : checkIndices) {
                Comparable<?> value = (Comparable<?>)arguments[index];

                if(!range.contains(value)) {
                    return new ImmutablePair<>(false, String.format("The provided value '%s' at index '%s' is not " +
                            "within required range '%s'", value, index, range.toString()));
                }
            }

            return new ImmutablePair<>(true, null);
        });
    }
}
