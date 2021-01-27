package io.github.regularcommands.util;

public class ArrayUtils {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static <T> boolean isArrayIndexValid(T[] array, int index) {
        return index >= 0 && index < array.length;
    }
}
