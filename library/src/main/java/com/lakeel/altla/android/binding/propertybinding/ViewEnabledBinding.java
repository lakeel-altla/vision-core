package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Property;

import android.support.annotation.NonNull;
import android.view.View;

public final class ViewEnabledBinding extends AbstractPropertyBinding<View> {

    public ViewEnabledBinding(@NonNull View view, @NonNull Property<?> property) {
        super(view, property);
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
        Boolean value = (Boolean) getConverter().convert(getProperty().getAsObject());
        getView().setEnabled(value);
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
