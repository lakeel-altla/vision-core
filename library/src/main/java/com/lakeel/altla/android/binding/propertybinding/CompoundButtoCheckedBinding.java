package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.BooleanProperty;

import android.support.annotation.NonNull;
import android.widget.CompoundButton;

public final class CompoundButtoCheckedBinding
        extends AbstractPropertyBinding<CompoundButton, BooleanProperty> {

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public CompoundButtoCheckedBinding(@NonNull CompoundButton compoundButton, @NonNull BooleanProperty property) {
        super(compoundButton, property);
    }

    @Override
    protected BindingMode getDefaultBindingMode() {
        return BindingMode.TWO_WAY;
    }

    @Override
    protected boolean isSourceToTargetBindingSupported() {
        return true;
    }

    @Override
    protected boolean isTargetToSourceBindingSupported() {
        return true;
    }

    @Override
    protected void updateTargetCore() {
        getView().setChecked(getProperty().get());
    }

    @Override
    protected void updateSourceCore() {
        getProperty().set(getView().isChecked());
    }

    @Override
    protected void bindSource() {
        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSource();
            }
        };
        getView().setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    protected void unbindSource() {
        getView().setOnCheckedChangeListener(null);
        onCheckedChangeListener = null;
    }
}
