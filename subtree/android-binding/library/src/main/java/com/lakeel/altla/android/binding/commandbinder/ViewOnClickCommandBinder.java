package com.lakeel.altla.android.binding.commandbinder;

import com.lakeel.altla.android.binding.Command;

import android.support.annotation.NonNull;
import android.view.View;

public final class ViewOnClickCommandBinder extends AbstractCommandBinder {

    private View.OnClickListener onClickListener;

    public ViewOnClickCommandBinder(@NonNull CommandBindingDefinition definition, @NonNull View target,
                                    @NonNull Command source) {
        super(definition, target, source);
    }

    @Override
    protected void bindSource() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSource().execute();
            }
        };
        getTarget().setOnClickListener(onClickListener);
    }

    @Override
    protected void unbindSource() {
        if (onClickListener != null) getTarget().setOnClickListener(null);
    }
}
