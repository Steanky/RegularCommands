package io.github.regularcommands.util;

import io.github.regularcommands.converter.ArgumentConverter;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Material;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Utility class containing built-in converters and helper methods.
 */
public final class Converters {
    public final static ArgumentConverter<BigDecimal> BIG_DECIMAL_CONVERTER = argument -> {
        Pair<Boolean, BigDecimal> conversionResult = tryParseBigDecimal(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a BigDecimal.", argument));
    };

    public final static ArgumentConverter<BigInteger> BIG_INTEGER_CONVERTER = argument -> {
        Pair<Boolean, BigInteger> conversionResult = tryParseBigInteger(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a BigInteger.", argument));
    };

    public final static ArgumentConverter<Long> LONG_CONVERTER = argument -> {
        Pair<Boolean, Long> conversionResult = tryParseLong(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Long.", argument));
    };

    public final static ArgumentConverter<Integer> INTEGER_CONVERTER = argument -> {
        Pair<Boolean, Integer> conversionResult = tryParseInt(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to an Integer.", argument));
    };

    public final static ArgumentConverter<Double> DOUBLE_CONVERTER = argument -> {
        Pair<Boolean, Double> conversionResult = tryParseDouble(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Double.", argument));
    };

    public final static ArgumentConverter<Float> FLOAT_CONVERTER = argument -> {
        Pair<Boolean, Float> conversionResult = tryParseFloat(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Float.", argument));
    };

    public final static ArgumentConverter<Short> SHORT_CONVERTER = argument -> {
        Pair<Boolean, Short> conversionResult = tryParseShort(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Short.", argument));
    };

    public final static ArgumentConverter<Byte> BYTE_CONVERTER = argument -> {
        Pair<Boolean, Byte> conversionResult = tryParseByte(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Byte.", argument));
    };

    public final static ArgumentConverter<Boolean> BOOLEAN_CONVERTER = argument -> {
        Pair<Boolean, Boolean> conversionResult = tryParseBoolean(argument);

        if(conversionResult.getLeft()) {
            return ImmutableTriple.of(true, conversionResult.getRight(), null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Boolean.", argument));
    };

    public static final ArgumentConverter<Material> MATERIAL_CONVERTER = argument -> {
        Material material = Material.getMaterial(argument);

        if(material != null) {
            return ImmutableTriple.of(true, material, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Material.", argument));
    };

    /**
     * Converts any generic IArgumentConverter into an IArgumentConverter of generic type Object.
     * @param original The IArgumentConverter to convert
     * @return An IArgumentConverter of generic type Object
     */
    public static ArgumentConverter<Object> asObjectConverter(ArgumentConverter<?> original) {
        return argument -> {
            Triple<Boolean, ?, String> originalConversion = original.convert(argument);
            return ImmutableTriple.of(originalConversion.getLeft(), originalConversion.getMiddle(), originalConversion.getRight());
        };
    }

    /**
     * Creates an ArgumentConverter that can convert an input sequence into an array, given an ArgumentConverter
     * that is capable of converting individual arguments, and a delimiter to split the input string.
     * @param elementConverter The converter that will convert each element
     * @param delimiter The delimiter used to split up the input string
     * @param arrayType The type of the array
     * @param <T> The type of argument we're trying to convert
     * @return An argument converter capable of transforming an input string into an array
     */
    public static <T> ArgumentConverter<T[]> newArrayConverter(ArgumentConverter<T> elementConverter, String delimiter,
                                                               Class<T> arrayType) {
        Objects.requireNonNull(elementConverter, "element converter cannot be null");
        Objects.requireNonNull(delimiter, "delimiter cannot be null");
        Objects.requireNonNull(arrayType, "arrayType cannot be null");

        return argument -> {
            String[] components = argument.split(delimiter);
            if(components.length == 0) {
                //noinspection unchecked
                return ImmutableTriple.of(true, (T[])Array.newInstance(arrayType, 0), null);
            }

            //noinspection unchecked
            T[] resultingArray = (T[])Array.newInstance(arrayType, components.length);

            for(int i = 0; i < components.length; i++) {
                String component = components[i];
                Triple<Boolean, T, String> result = elementConverter.convert(component);

                if(result.getLeft()) {
                    resultingArray[i] = result.getMiddle();
                }
                else {
                    return ImmutableTriple.of(false, null, result.getRight());
                }
            }

            return ImmutableTriple.of(true, resultingArray, null);
        };
    }


    private static Pair<Boolean, BigDecimal> tryParseBigDecimal(String value) {
        try {
            return ImmutablePair.of(true, new BigDecimal(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, BigDecimal.ZERO);
        }
    }

    private static Pair<Boolean, BigInteger> tryParseBigInteger(String value) {
        try {
            return ImmutablePair.of(true, new BigInteger(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, BigInteger.ZERO);
        }
    }

    private static Pair<Boolean, Long> tryParseLong(String value) {
        try {
            return ImmutablePair.of(true, Long.parseLong(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, 0L);
        }
    }

    private static Pair<Boolean, Integer> tryParseInt(String value) {
        try {
            return ImmutablePair.of(true, Integer.parseInt(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, 0);
        }
    }

    private static Pair<Boolean, Double> tryParseDouble(String value) {
        try {
            return ImmutablePair.of(true, Double.parseDouble(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, 0D);
        }
    }

    private static Pair<Boolean, Float> tryParseFloat(String value) {
        try {
            return ImmutablePair.of(true, Float.parseFloat(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, 0F);
        }
    }

    private static Pair<Boolean, Short> tryParseShort(String value) {
        try {
            return ImmutablePair.of(true, Short.parseShort(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, (short)0);
        }
    }

    private static Pair<Boolean, Byte> tryParseByte(String value) {
        try {
            return ImmutablePair.of(true, Byte.parseByte(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, (byte)0);
        }
    }

    private static Pair<Boolean, Boolean> tryParseBoolean(String value) {
        if(value.equalsIgnoreCase("true")) {
            return ImmutablePair.of(true, true);
        }
        else if(value.equalsIgnoreCase("false")) {
            return ImmutablePair.of(true, false);
        }
        else {
            return ImmutablePair.of(false, false);
        }
    }
}