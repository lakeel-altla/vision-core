package com.lakeel.altla.android.binding.target;

import com.lakeel.altla.android.binding.Property;
import com.lakeel.altla.android.binding.propertybinding.EditTextTextPropertyBinding;

import android.support.annotation.NonNull;
import android.widget.EditText;

public final class EditTextTarget {

    private final EditText editText;

    public EditTextTarget(@NonNull EditText editText) {
        this.editText = editText;
    }

    @NonNull
    public EditTextTextPropertyBinding text(@NonNull Property<?> property) {
        return new EditTextTextPropertyBinding(editText, property);
    }
}
