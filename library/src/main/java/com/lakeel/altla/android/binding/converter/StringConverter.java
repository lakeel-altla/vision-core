package com.lakeel.altla.android.binding.converter;

import com.lakeel.altla.android.binding.Converter;

public final class StringConverter implements Converter {

    public static StringConverter INSTANCE = new StringConverter();

    private StringConverter() {
    }

    @Override
    public Object convert(Object value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Object convertBack(Object value) {
        return value == null ? null : value.toString();
    }
}
