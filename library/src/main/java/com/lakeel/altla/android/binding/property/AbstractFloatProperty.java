package com.lakeel.altla.android.binding.property;

import android.support.annotation.Nullable;

public abstract class AbstractFloatProperty extends BaseProperty {

    protected AbstractFloatProperty() {
    }

    public abstract float get();

    public abstract void set(float value);

    @Nullable
    @Override
    public final Object getAsObject() {
        return get();
    }

    @Override
    public final void setAsObject(@Nullable Object value) {
        set(value == null ? 0 : (float) value);
    }
}
