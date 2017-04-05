package com.lakeel.altla.android.binding.commandbinder;

import com.lakeel.altla.android.binding.Command;

import android.support.annotation.NonNull;
import android.view.View;

public final class ViewOnLongClickCommandBinder extends AbstractCommandBinder {

    private View.OnLongClickListener onLongClickListener;

    public ViewOnLongClickCommandBinder(@NonNull CommandBindingDefinition definition, @NonNull View target,
                                        @NonNull Command source) {
        super(definition, target, source);
    }

    @Override
    protected void bindSource() {
        onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getSource().execute();
                return true;
            }
        };
        getTarget().setOnLongClickListener(onLongClickListener);
    }

    @Override
    protected void unbindSource() {
        if (onLongClickListener != null) getTarget().setOnLongClickListener(null);
    }
}
