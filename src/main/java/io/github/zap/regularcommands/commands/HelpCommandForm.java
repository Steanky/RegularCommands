package io.github.zap.regularcommands.commands;

import io.github.zap.regularcommands.converter.Parameter;
import io.github.zap.regularcommands.util.Converters;
import io.github.zap.regularcommands.validator.CommandValidator;
import io.github.zap.regularcommands.validator.ValidationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HelpCommandForm extends CommandForm<Component> {
    private static final Parameter[] parameters = new Parameter[] {
            new Parameter("help"),
            new Parameter("^[1-9]\\d*$", Converters.INTEGER_CONVERTER)
    };

    private static final CommandValidator<Component, ?> validator = new CommandValidator<>(
            (context, arguments, previousData) -> {
        int index = (int)arguments[1] - 1;
        PageBuilder pageBuilder = context.getForm().getCommand().getPageBuilder();

        if(index < pageBuilder.pageCount()) {
            return ValidationResult.of(true, null, pageBuilder.getPage(index));
        }

        return ValidationResult.of(true, Component.translatable(DefaultKeys.ERROR_PAGE_INDEX_OUT_OF_BOUNDS.key(),
                Component.text(index)), null);
    });

    public HelpCommandForm(@NotNull RegularCommand command, @NotNull Component usage,
                           @NotNull PermissionData permissionData) {
        super(command, usage, permissionData, parameters);
    }

    @Override
    public @Nullable CommandValidator<Component, ?> getValidator(Context context, Object[] arguments) {
        return validator;
    }

    @Override
    public @Nullable Component execute(Context context, Object[] arguments, Component display) {
        return display;
    }
}
