package io.github.regularcommands.message;

import org.jetbrains.annotations.NotNull;

public enum DefaultMessages implements MessageKey {
    ERROR_NO_PERMISSION("message.error.no_permission"),
    ERROR_CONVERT_BIG_DECIMAL("message.error.convert.big_decimal"),
    ERROR_CONVERT_BIG_INTEGER("message.error.convert.big_integer"),
    ERROR_CONVERT_LONG("message.error.convert.long"),
    ERROR_CONVERT_INTEGER("message.error.convert.integer"),
    ERROR_CONVERT_DOUBLE("message.error.convert.double"),
    ERROR_CONVERT_FLOAT("message.error.convert.float"),
    ERROR_CONVERT_SHORT("message.error.convert.short"),
    ERROR_CONVERT_BYTE("message.error.convert.byte"),
    ERROR_CONVERT_BOOLEAN("message.error.convert.boolean"),
    ERROR_CONVERT_MATERIAL("message.error.convert.material"),
    ERROR_ENTITY_EXECUTOR("message.error.executor.entity"),
    ERROR_PLAYER_EXECUTOR("message.error.executor.player"),
    ERROR_CONSOLE_EXECUTOR("message.error.executor.console"),
    ERROR_BLOCK_EXECUTOR("message.error.executor.block");

    private final String key;

    DefaultMessages(@NotNull String key) {
        this.key = key;
    }

    @Override
    public @NotNull String key() {
        return key;
    }
}
