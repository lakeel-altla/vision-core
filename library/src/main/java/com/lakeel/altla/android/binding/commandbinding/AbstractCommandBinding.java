package com.lakeel.altla.android.binding.commandbinding;

import com.lakeel.altla.android.binding.Command;
import com.lakeel.altla.android.binding.Unbindable;

import android.support.annotation.NonNull;
import android.view.View;

public abstract class AbstractCommandBinding<TView extends View> implements Unbindable {

    private final TView view;

    private final Command command;

    private Command.OnCanExecuteChangedListener onCanExecuteChangedListener;

    protected AbstractCommandBinding(@NonNull TView view, @NonNull Command command) {
        this.view = view;
        this.command = command;
    }

    @Override
    public final void unbind() {
        if (onCanExecuteChangedListener != null) command.removeOnCanExecuteChangedListener(onCanExecuteChangedListener);
        unbindSource();
    }

    @NonNull
    public final Unbindable bind() {
        updateTarget();
        bindTarget();
        bindSource();
        return this;
    }

    @NonNull
    protected TView getView() {
        return view;
    }

    @NonNull
    protected Command getCommand() {
        return command;
    }

    protected abstract void bindSource();

    protected abstract void unbindSource();

    private void updateTarget() {
        view.setEnabled(command.canExecute());
    }

    private void bindTarget() {
        onCanExecuteChangedListener = new Command.OnCanExecuteChangedListener() {
            @Override
            public void onCanExecuteChanged() {
                view.setEnabled(command.canExecute());
            }
        };
        command.addOnCanExecuteChangedListener(onCanExecuteChangedListener);
    }
}
