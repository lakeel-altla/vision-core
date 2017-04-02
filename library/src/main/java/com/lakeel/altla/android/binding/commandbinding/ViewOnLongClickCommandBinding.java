package com.lakeel.altla.android.binding.commandbinding;

import com.lakeel.altla.android.binding.Command;

import android.support.annotation.NonNull;
import android.view.View;

public final class ViewOnLongClickCommandBinding extends AbstractCommandBinding<View> {

    private View.OnLongClickListener onLongClickListener;

    public ViewOnLongClickCommandBinding(@NonNull View view, @NonNull Command command) {
        super(view, command);
    }

    @Override
    protected void bindSource() {
        onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getCommand().execute();
                return true;
            }
        };
        getView().setOnLongClickListener(onLongClickListener);
    }

    @Override
    protected void unbindSource() {
        getView().setOnLongClickListener(null);
        onLongClickListener = null;
    }
}
