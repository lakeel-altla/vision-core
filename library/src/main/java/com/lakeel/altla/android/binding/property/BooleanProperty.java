package com.lakeel.altla.android.binding.property;

import android.support.annotation.Nullable;

public abstract class BooleanProperty extends BaseProperty {

    protected BooleanProperty() {
    }

    public abstract boolean get();

    public abstract void set(boolean value);

    @Nullable
    @Override
    public final Object getAsObject() {
        return get();
    }

    @Override
    public final void setAsObject(@Nullable Object value) {
        set(value == null ? false : (Boolean) value);
    }
}
