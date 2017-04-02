package com.lakeel.altla.android.binding.target;

import com.lakeel.altla.android.binding.ObjectProperty;
import com.lakeel.altla.android.binding.propertybinding.TextViewTextPropertyBinding;

import android.support.annotation.NonNull;
import android.widget.TextView;

public final class TextViewTarget {

    private final TextView textView;

    public TextViewTarget(@NonNull TextView textView) {
        this.textView = textView;
    }

    @NonNull
    public TextViewTextPropertyBinding text(@NonNull ObjectProperty<?> property) {
        return new TextViewTextPropertyBinding(textView, property);
    }
}
