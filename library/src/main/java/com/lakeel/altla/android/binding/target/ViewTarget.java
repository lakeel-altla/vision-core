package com.lakeel.altla.android.binding.target;

import com.lakeel.altla.android.binding.Command;
import com.lakeel.altla.android.binding.commandbinding.ViewOnClickCommandBinding;
import com.lakeel.altla.android.binding.commandbinding.ViewOnLongClickCommandBinding;

import android.support.annotation.NonNull;
import android.view.View;

public final class ViewTarget {

    private final View view;

    public ViewTarget(@NonNull View view) {
        this.view = view;
    }

    @NonNull
    public ViewOnClickCommandBinding click(@NonNull Command command) {
        return new ViewOnClickCommandBinding(view, command);
    }

    @NonNull
    public ViewOnLongClickCommandBinding longClick(@NonNull Command command) {
        return new ViewOnLongClickCommandBinding(view, command);
    }
}
