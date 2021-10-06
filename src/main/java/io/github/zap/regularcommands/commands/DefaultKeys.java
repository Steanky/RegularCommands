package io.github.zap.regularcommands.commands;

import org.jetbrains.annotations.NotNull;

public enum DefaultKeys {
    NAVBAR("feedback.page.navbar", "-==Page {0}/{1}==-"),
    ERROR_PAGE_INDEX_OUT_OF_BOUNDS("feedback.error.command.help.index_out_of_bounds", "Index {0} out of bounds!"),
    ERROR_ENTITY_EXECUTOR("feedback.error.entity_executor", "Only entities can execute this command!"),
    ERROR_PLAYER_EXECUTOR("feedback.error.player_executor", "Only players can execute this command!"),
    ERROR_CONSOLE_EXECUTOR("feedback.error.console_executor", "Only the console can execute this command!"),
    ERROR_BLOCK_EXECUTOR("feedback.error.block_executor", "Only blocks can execute this command!"),
    ERROR_NO_PERMISSION("feedback.error.no_permission", "You do not have permission to execute this command!"),
    ERROR_NO_FORMS("feedback.error.no_forms", "No matching forms exist for that command!"),
    ERROR_CONVERT_BIG_DECIMAL("feedback.error.convert.big_decimal", "Value {0} cannot be converted to a BigDecimal!"),
    ERROR_CONVERT_BIG_INTEGER("feedback.error.convert.big_integer", "Value {0} cannot be converted to a BigInteger!"),
    ERROR_CONVERT_LONG("feedback.error.convert.long", "Value {0} cannot be converted to a long!"),
    ERROR_CONVERT_INTEGER("feedback.error.convert.integer", "Value {0} cannot be converted to an int!"),
    ERROR_CONVERT_DOUBLE("feedback.error.convert.double", "Value {0} cannot be converted to a double!"),
    ERROR_CONVERT_FLOAT("feedback.error.convert.float", "Value {0} cannot be converted to a float!"),
    ERROR_CONVERT_SHORT("feedback.error.convert.short", "Value {0} cannot be converted to a short!"),
    ERROR_CONVERT_BYTE("feedback.error.convert.byte", "Value {0} cannot be converted to a byte!"),
    ERROR_CONVERT_BOOLEAN("feedback.error.convert.boolean", "Value {0} cannot be converted to a boolean!"),
    ERROR_CONVERT_MATERIAL("feedback.error.convert.material","Value {0} cannot be converted to a Material!");

    private final String key;
    private final String defaultPattern;

    DefaultKeys(String key, String defaultPattern) {
        this.key = key;
        this.defaultPattern = defaultPattern;
    }

    public @NotNull String key() {
        return key;
    }

    public @NotNull String getPattern() {
        return defaultPattern;
    }
}
