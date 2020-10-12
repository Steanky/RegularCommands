package io.github.regularcommands.converter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility class containing built-in converters and helper methods.
 */
public final class Converters {
    public final static ArgumentConverter<String> STRING_CONVERTER = argument -> ImmutableTriple.of(true, argument, null);

    public final static ArgumentConverter<BigDecimal> BIG_DECIMAL_CONVERTER = argument -> {
        ImmutablePair<Boolean, BigDecimal> conversionResult = tryParseBigDecimal(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a BigDecimal.", argument));
    };

    public final static ArgumentConverter<BigInteger> BIG_INTEGER_CONVERTER = argument -> {
        ImmutablePair<Boolean, BigInteger> conversionResult = tryParseBigInteger(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a BigInteger.", argument));
    };

    public final static ArgumentConverter<Long> LONG_CONVERTER = argument -> {
        ImmutablePair<Boolean, Long> conversionResult = tryParseLong(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Long.", argument));
    };

    public final static ArgumentConverter<Integer> INTEGER_CONVERTER = argument -> {
        ImmutablePair<Boolean, Integer> conversionResult = tryParseInt(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to an Integer.", argument));
    };

    public final static ArgumentConverter<Double> DOUBLE_CONVERTER = argument -> {
        ImmutablePair<Boolean, Double> conversionResult = tryParseDouble(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Double.", argument));
    };

    public final static ArgumentConverter<Float> FLOAT_CONVERTER = argument -> {
        ImmutablePair<Boolean, Float> conversionResult = tryParseFloat(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Float.", argument));
    };

    public final static ArgumentConverter<Short> SHORT_CONVERTER = argument -> {
        ImmutablePair<Boolean, Short> conversionResult = tryParseShort(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Short.", argument));
    };

    public final static ArgumentConverter<Byte> BYTE_CONVERTER = argument -> {
        ImmutablePair<Boolean, Byte> conversionResult = tryParseByte(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Byte.", argument));
    };

    public final static ArgumentConverter<Boolean> BOOLEAN_CONVERTER = argument -> {
        ImmutablePair<Boolean, Boolean> conversionResult = tryParseBoolean(argument);

        if(conversionResult.left) {
            return ImmutableTriple.of(true, conversionResult.right, null);
        }

        return ImmutableTriple.of(false, null, String.format("The provided value '%s' cannot be converted to a Boolean.", argument));
    };

    /**
     * Converts any generic IArgumentConverter into an IArgumentConverter of generic type Object.
     * @param original The IArgumentConverter to convert
     * @return An IArgumentConverter of generic type Object
     */
    public static ArgumentConverter<Object> asObjectConverter(ArgumentConverter<?> original) {
        return argument -> {
            ImmutableTriple<Boolean, ?, String> originalConversion = original.convert(argument);
            return ImmutableTriple.of(originalConversion.left, originalConversion.middle, originalConversion.right);
        };
    }

    public static ImmutablePair<Boolean, BigDecimal> tryParseBigDecimal(String value) {
        try {
            return ImmutablePair.of(true, new BigDecimal(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, BigDecimal.ZERO);
        }
    }

    public static ImmutablePair<Boolean, BigInteger> tryParseBigInteger(String value) {
        try {
            return ImmutablePair.of(true, new BigInteger(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, BigInteger.ZERO);
        }
    }

    public static ImmutablePair<Boolean, Long> tryParseLong(String value) {
        try {
            return ImmutablePair.of(true, Long.parseLong(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, 0L);
        }
    }

    public static ImmutablePair<Boolean, Integer> tryParseInt(String value) {
        try {
            return ImmutablePair.of(true, Integer.parseInt(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, 0);
        }
    }

    public static ImmutablePair<Boolean, Double> tryParseDouble(String value) {
        try {
            return ImmutablePair.of(true, Double.parseDouble(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, 0D);
        }
    }

    public static ImmutablePair<Boolean, Float> tryParseFloat(String value) {
        try {
            return ImmutablePair.of(true, Float.parseFloat(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, 0F);
        }
    }

    public static ImmutablePair<Boolean, Short> tryParseShort(String value) {
        try {
            return ImmutablePair.of(true, Short.parseShort(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, (short)0);
        }
    }

    public static ImmutablePair<Boolean, Byte> tryParseByte(String value) {
        try {
            return ImmutablePair.of(true, Byte.parseByte(value));
        }
        catch(NumberFormatException e) {
            return ImmutablePair.of(false, (byte)0);
        }
    }

    public static ImmutablePair<Boolean, Boolean> tryParseBoolean(String value) {
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