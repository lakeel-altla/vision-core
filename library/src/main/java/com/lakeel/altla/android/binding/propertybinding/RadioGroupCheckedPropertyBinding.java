package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.IntProperty;

import android.support.annotation.NonNull;
import android.widget.RadioGroup;

public final class RadioGroupCheckedPropertyBinding extends AbstractPropertyBinding<RadioGroup, IntProperty> {

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

    public RadioGroupCheckedPropertyBinding(@NonNull RadioGroup radioGroup, @NonNull IntProperty property) {
        super(radioGroup, property);
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
        getView().check(getProperty().get());
    }

    @Override
    protected void updateSourceCore() {
        getProperty().set(getView().getCheckedRadioButtonId());
    }

    @Override
    protected void bindSource() {
        onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateSource();
            }
        };
        getView().setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    protected void unbindSource() {
        if (onCheckedChangeListener != null) getView().setOnCheckedChangeListener(null);
        onCheckedChangeListener = null;
    }
}
