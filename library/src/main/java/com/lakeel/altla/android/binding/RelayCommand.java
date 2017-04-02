package com.lakeel.altla.android.binding;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class RelayCommand extends AbstractCommand {

    private final ExecuteDelegate executeDelegate;

    private final CanExecuteDelegate canExecuteDelegate;

    public RelayCommand(@NonNull ExecuteDelegate executeDelegate) {
        this(executeDelegate, null);
    }

    public RelayCommand(@NonNull ExecuteDelegate executeDelegate,
                        @Nullable CanExecuteDelegate canExecuteDelegate) {
        this.executeDelegate = executeDelegate;
        this.canExecuteDelegate = canExecuteDelegate;
    }

    @Override
    public final void execute() {
        executeDelegate.execute();
    }

    @Override
    public final boolean canExecute() {
        return canExecuteDelegate == null || canExecuteDelegate.canExecute();
    }

    public interface ExecuteDelegate {

        void execute();
    }

    public interface CanExecuteDelegate {

        boolean canExecute();
    }
}
