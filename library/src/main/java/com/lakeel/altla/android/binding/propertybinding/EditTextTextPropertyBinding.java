package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Property;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public final class EditTextTextPropertyBinding extends AbstractPropertyBinding<EditText> {

    private TextWatcher textWatcher;

    public EditTextTextPropertyBinding(@NonNull EditText editText, @NonNull Property<?> property) {
        super(editText, property);
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
        String value = (String) getConverter().convert(getProperty().getAsObject());
        getView().setText(value == null ? null : value);
    }

    @Override
    protected void updateSourceCore() {
        Editable editable = getView().getText();
        Object value = getConverter().convertBack(editable == null ? null : editable.toString());
        getProperty().setAsObject(value);
    }

    @Override
    protected void bindSource() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSource();
            }
        };
        getView().addTextChangedListener(textWatcher);
    }

    @Override
    protected void unbindSource() {
        if (textWatcher != null) getView().removeTextChangedListener(textWatcher);
        textWatcher = null;
    }
}
