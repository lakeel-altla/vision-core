package com.lakeel.altla.android.binding;

import android.support.annotation.NonNull;

public interface PropertyBinder {

    PropertyBinder mode(@NonNull BindingMode mode);

    PropertyBinder converter(@NonNull Converter converter);

    Unbindable bind();
}
