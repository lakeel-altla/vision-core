package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Property;

import android.support.annotation.NonNull;
import android.widget.CompoundButton;

public final class CompoundButtoCheckedBinding extends AbstractPropertyBinding<CompoundButton> {

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public CompoundButtoCheckedBinding(@NonNull CompoundButton compoundButton, @NonNull Property<?> property) {
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
        Boolean value = (Boolean) getConverter().convert(getProperty().getAsObject());
        getView().setChecked(value);
    }

    @Override
    protected void updateSourceCore() {
        Object value = getConverter().convertBack(getView().isChecked());
        getProperty().setAsObject(value);
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
