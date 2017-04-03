package com.lakeel.altla.android.binding.target;

import com.lakeel.altla.android.binding.Property;
import com.lakeel.altla.android.binding.propertybinding.CompoundButtoCheckedBinding;

import android.support.annotation.NonNull;
import android.widget.CompoundButton;

public final class CompoundButtonTarget {

    private final CompoundButton compoundButton;

    public CompoundButtonTarget(@NonNull CompoundButton compoundButton) {
        this.compoundButton = compoundButton;
    }

    @NonNull
    public CompoundButtoCheckedBinding checked(@NonNull Property<?> property) {
        return new CompoundButtoCheckedBinding(compoundButton, property);
    }
}
