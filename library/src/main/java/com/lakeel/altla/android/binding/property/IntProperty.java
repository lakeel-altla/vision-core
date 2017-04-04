package com.lakeel.altla.android.binding.property;

public class IntProperty extends AbstractIntProperty {

    private int value;

    public IntProperty() {
    }

    public IntProperty(int value) {
        this.value = value;
    }

    @Override
    public final int get() {
        return value;
    }

    @Override
    public final void set(int value) {
        if (this.value != value) {
            this.value = value;
            onValueChanged();
        }
    }

    protected void onValueChanged() {
        raiseOnValueChanged();
    }
}
