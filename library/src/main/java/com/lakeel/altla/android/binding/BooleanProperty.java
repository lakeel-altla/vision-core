package com.lakeel.altla.android.binding;

public abstract class BooleanProperty extends BaseProperty {

    protected BooleanProperty() {
    }

    public abstract boolean get();

    public abstract void set(boolean value);
}
