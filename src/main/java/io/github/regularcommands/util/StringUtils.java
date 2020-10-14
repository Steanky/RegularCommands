package io.github.regularcommands.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contains a few useful methods for processing strings.
 */
public final class StringUtils {
    /**
     * Splits an input string around the specified delimiter character, which can be escaped.
     * @param input The string to be split
     * @param split The character used as the delimiter
     * @param escapeChar The character used to escape the delimiter
     * @return An array containing each portion of the string, split around the delimiter
     */
    public static String[] splitWithEscape(String input, char split, char escapeChar) {
        Objects.requireNonNull(input, "input cannot be null");
        Validate.isTrue(split != escapeChar, "split and escapeChar cannot be the same character");

        StringBuilder buffer = new StringBuilder();
        List<String> result = new ArrayList<>();

        boolean escape = false;
        for(char sample : input.toCharArray()) {
            if(escape) {
                escape = false;
                buffer.append(sample);
            }
            else {
                if(sample == escapeChar) {
                    escape = true;
                }
                else if(sample == split) {
                    if(buffer.length() > 0) {
                        result.add(buffer.toString());
                        buffer.setLength(0);
                    }
                }
                else {
                    buffer.append(sample);
                }
            }
        }

        if(buffer.length() > 0) {
            result.add(buffer.toString());
        }

        return result.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Escapes a string that may contain TextStylizer formatting information.
     * @param input The string to escape
     * @return A string that contains no special formatting information
     */
    public static String escapify(String input) {
        Objects.requireNonNull(input, "input cannot be null");
        StringBuilder builder = new StringBuilder();

        for(char character : input.toCharArray()) {
            switch(character) {
                case '{':
                case '}':
                case '>':
                case '|':
                case '\\':
                    builder.append('\\').append(character);
                    break;
                default:
                    builder.append(character);
                    break;
            }
        }

        return builder.toString();
    }
}
