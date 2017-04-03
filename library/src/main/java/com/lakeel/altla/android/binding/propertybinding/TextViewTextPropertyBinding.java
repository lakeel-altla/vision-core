package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Property;

import android.support.annotation.NonNull;
import android.widget.TextView;

public final class TextViewTextPropertyBinding extends AbstractPropertyBinding<TextView> {

    public TextViewTextPropertyBinding(@NonNull TextView textView, @NonNull Property<?> property) {
        super(textView, property);
    }

    @Override
    protected BindingMode getDefaultBindingMode() {
        return BindingMode.ONE_WAY;
    }

    @Override
    protected boolean isSourceToTargetBindingSupported() {
        return true;
    }

    @Override
    protected boolean isTargetToSourceBindingSupported() {
        return false;
    }

    @Override
    protected void updateTargetCore() {
        String value = (String) getConverter().convert(getProperty().getAsObject());
        getView().setText(value == null ? null : value);
    }

    @Override
    protected void updateSourceCore() {
    }

    @Override
    protected void bindSource() {
    }

    @Override
    protected void unbindSource() {
    }
}
