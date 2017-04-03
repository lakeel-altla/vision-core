package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Property;

import android.support.annotation.NonNull;
import android.widget.RadioGroup;

public final class RadioGroupCheckedPropertyBinding extends AbstractPropertyBinding<RadioGroup> {

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

    public RadioGroupCheckedPropertyBinding(@NonNull RadioGroup radioGroup, @NonNull Property<?> property) {
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
        Integer value = (Integer) getConverter().convert(getProperty().getAsObject());
        getView().check(value == null ? 0 : value);
    }

    @Override
    protected void updateSourceCore() {
        Object value = getConverter().convertBack(getView().getCheckedRadioButtonId());
        getProperty().setAsObject(value);
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
