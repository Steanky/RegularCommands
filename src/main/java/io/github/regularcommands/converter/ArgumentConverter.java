package io.github.regularcommands.converter;

import org.apache.commons.lang3.tuple.Triple;

public interface ArgumentConverter<T> {
    /**
     * Converts the argument into a type of object, returning information about the success of the conversion, the
     * object itself, and a user-friendly error message in the event that the conversion fails.
     * @param argument The argument to be converted
     * @return An ImmutableTriple object whose first parameter corresponds to the success of the conversion. The second
     * parameter contains the converted object (which will be null if the conversion failed), and the third parameter
     * contains a user-friendly error message. It will be null if the conversion was a success.
     */
    Triple<Boolean, T, String> convert(String argument);
}
