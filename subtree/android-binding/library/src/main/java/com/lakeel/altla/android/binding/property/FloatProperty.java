package com.lakeel.altla.android.binding.property;

public class FloatProperty extends AbstractFloatProperty {

    private float value;

    public FloatProperty() {
    }

    public FloatProperty(float value) {
        this.value = value;
    }

    @Override
    public final float get() {
        return value;
    }

    @Override
    public final void set(float value) {
        if (this.value != value) {
            this.value = value;
            onValueChanged();
        }
    }

    protected void onValueChanged() {
        raiseOnValueChanged();
    }
}
