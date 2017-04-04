package com.lakeel.altla.android.binding.commandbinder;

import com.lakeel.altla.android.binding.Command;
import com.lakeel.altla.android.binding.CommandBinder;
import com.lakeel.altla.android.binding.Unbindable;

import android.support.annotation.NonNull;
import android.view.View;

public abstract class AbstractCommandBinder implements CommandBinder, Unbindable {

    private final CommandBindingDefinition definition;

    private final View target;

    private final Command source;

    private Command.OnCanExecuteChangedListener onCanExecuteChangedListener;

    protected AbstractCommandBinder(@NonNull CommandBindingDefinition definition, @NonNull View target,
                                    @NonNull Command source) {
        this.definition = definition;
        this.target = target;
        this.source = source;
    }

    @Override
    public Unbindable bind() {
        updateTarget();
        bindSource();
        bindTarget();

        return this;
    }

    @Override
    public void unbind() {
        if (onCanExecuteChangedListener != null) source.removeOnCanExecuteChangedListener(onCanExecuteChangedListener);
        unbindSource();
    }

    @NonNull
    protected CommandBindingDefinition getDefinition() {
        return definition;
    }

    @NonNull
    protected View getTarget() {
        return target;
    }

    @NonNull
    protected Command getSource() {
        return source;
    }

    protected final void updateTarget() {
        updateTargetCore();
    }

    protected void updateTargetCore() {
        target.setEnabled(source.canExecute());
    }

    protected abstract void bindSource();

    protected abstract void unbindSource();

    private void bindTarget() {
        onCanExecuteChangedListener = new Command.OnCanExecuteChangedListener() {
            @Override
            public void onCanExecuteChanged() {
                updateTarget();
            }
        };
        source.addOnCanExecuteChangedListener(onCanExecuteChangedListener);
    }
}