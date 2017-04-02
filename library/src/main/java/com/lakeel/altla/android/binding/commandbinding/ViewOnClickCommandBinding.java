package com.lakeel.altla.android.binding.commandbinding;

import com.lakeel.altla.android.binding.Command;

import android.support.annotation.NonNull;
import android.view.View;

public final class ViewOnClickCommandBinding extends AbstractCommandBinding<View> {

    private View.OnClickListener onClickListener;

    public ViewOnClickCommandBinding(@NonNull View view, @NonNull Command command) {
        super(view, command);
    }

    @Override
    protected void bindSource() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().execute();
            }
        };
        getView().setOnClickListener(onClickListener);
    }

    @Override
    protected void unbindSource() {
        getView().setOnClickListener(null);
        onClickListener = null;
    }
}
