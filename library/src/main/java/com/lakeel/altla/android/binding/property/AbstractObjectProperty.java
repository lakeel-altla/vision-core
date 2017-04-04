package com.lakeel.altla.android.binding.property;

import android.support.annotation.Nullable;

public abstract class AbstractObjectProperty<T> extends BaseProperty {

    protected AbstractObjectProperty() {
    }

    @Nullable
    public abstract T get();

    public abstract void set(@Nullable T value);

    @Override
    public final Object getAsObject() {
        return get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void setAsObject(Object value) {
        set((T) value);
    }
}
