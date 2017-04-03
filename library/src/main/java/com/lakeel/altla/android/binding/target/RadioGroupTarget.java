package com.lakeel.altla.android.binding.target;

import com.lakeel.altla.android.binding.Property;
import com.lakeel.altla.android.binding.propertybinding.RadioGroupCheckedPropertyBinding;

import android.support.annotation.NonNull;
import android.widget.RadioGroup;

public final class RadioGroupTarget {

    private final RadioGroup radioGroup;

    public RadioGroupTarget(@NonNull RadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }

    @NonNull
    public RadioGroupCheckedPropertyBinding checked(@NonNull final Property<?> property) {
        RadioGroupCheckedPropertyBinding binding = new RadioGroupCheckedPropertyBinding(radioGroup, property);
        binding.bind();
        return binding;
    }
}
