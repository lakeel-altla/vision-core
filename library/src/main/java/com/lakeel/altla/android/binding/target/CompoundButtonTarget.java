package com.lakeel.altla.android.binding.target;

import com.lakeel.altla.android.binding.BooleanProperty;
import com.lakeel.altla.android.binding.propertybinding.CompoundButtoCheckedBinding;

import android.support.annotation.NonNull;
import android.widget.CompoundButton;

public final class CompoundButtonTarget {

    private final CompoundButton compoundButton;

    public CompoundButtonTarget(@NonNull CompoundButton compoundButton) {
        this.compoundButton = compoundButton;
    }

    @NonNull
    public CompoundButtoCheckedBinding checked(@NonNull BooleanProperty property) {
        return new CompoundButtoCheckedBinding(compoundButton, property);
    }
}
