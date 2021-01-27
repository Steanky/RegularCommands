package io.github.regularcommands.util;

import net.md_5.bungee.api.chat.TextComponent;

public class ArrayUtils {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final TextComponent[] EMPTY_TEXT_COMPONENT_ARRAY = new TextComponent[0];

    public static <T> boolean isArrayIndexValid(T[] array, int index) {
        return index >= 0 && index < array.length;
    }
}
