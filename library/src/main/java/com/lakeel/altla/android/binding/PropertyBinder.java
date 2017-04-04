package com.lakeel.altla.android.binding;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface PropertyBinder {

    PropertyBinder mode(@NonNull BindingMode mode);

    PropertyBinder converter(@NonNull Converter converter);

    PropertyBinder converter(@Nullable Convert convert, @Nullable ConvertBack convertBack);

    Unbindable bind();

    interface Convert {

        Object convert(Object value);
    }

    interface ConvertBack {

        Object convertBack(Object value);
    }
}
