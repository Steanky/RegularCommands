package io.github.zap.regularcommands.converter;

import io.github.zap.regularcommands.commands.CommandForm;
import org.jetbrains.annotations.NotNull;

public interface ArgumentConverter<T> {
    /**
     * Converts the argument into a type of object, returning information about the success of the conversion, the
     * object itself, and a user-friendly error message in the event that the conversion fails.
     * @param argument The argument to be converted
     * @return A ConversionResult object representing the result of the conversion
     */
    @NotNull ConversionResult<T> convert(@NotNull CommandForm<?> form, @NotNull String argument);
}
