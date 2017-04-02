package com.lakeel.altla.android.binding;

import android.support.annotation.NonNull;

public interface Property {

    void addOnValueChangedListener(@NonNull OnValueChangedListener listener);

    void removeOnValueChangedListener(@NonNull OnValueChangedListener listener);

    interface OnValueChangedListener {

        void onValueChanged();
    }
}
