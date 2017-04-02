package com.lakeel.altla.android.binding;

import android.support.annotation.Nullable;

public abstract class ObjectProperty<T> extends BaseProperty {

    protected ObjectProperty() {
    }

    @Nullable
    public abstract T get();

    public abstract void set(@Nullable T value);
}
