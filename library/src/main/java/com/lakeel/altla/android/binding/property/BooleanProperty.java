package com.lakeel.altla.android.binding.property;

public class BooleanProperty extends AbstractBooleanProperty {

    private boolean value;

    public BooleanProperty() {
    }

    public BooleanProperty(boolean value) {
        this.value = value;
    }

    @Override
    public final boolean get() {
        return value;
    }

    @Override
    public final void set(boolean value) {
        if (this.value != value) {
            this.value = value;
            onValueChanged();
        }
    }

    protected void onValueChanged() {
        raiseOnValueChanged();
    }
}
