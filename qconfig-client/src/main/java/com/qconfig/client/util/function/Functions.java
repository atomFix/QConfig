package com.qconfig.client.util.function;

import java.util.function.Function;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/24/11:02
 */
public interface Functions {

    Function<String, Integer> TO_INT_FUNCTION = Integer::parseInt;
    Function<String, Long> TO_LONG_FUNCTION = Long::parseLong;
    Function<String, Short> TO_SHORT_FUNCTION = Short::parseShort;
    Function<String, Byte> TO_BYTE_FUNCTION = Byte::parseByte;
    Function<String, Boolean> TO_BOOLEAN_FUNCTIO = Boolean::getBoolean;
    Function<String, Float> TO_FLOAT_FUNCTION = Float::parseFloat;
    Function<String, Double> TO_DOUBLE_FUNCTON = Double::parseDouble;

}
