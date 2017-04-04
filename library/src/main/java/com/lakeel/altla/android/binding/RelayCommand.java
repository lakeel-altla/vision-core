package com.lakeel.altla.android.binding;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class RelayCommand extends AbstractCommand {

    private final Execute execute;

    private final CanExecute canExecute;

    public RelayCommand(@NonNull Execute execute) {
        this(execute, null);
    }

    public RelayCommand(@NonNull Execute execute, @Nullable CanExecute canExecute) {
        this.execute = execute;
        this.canExecute = canExecute;
    }

    @Override
    public final void execute() {
        execute.execute();
    }

    @Override
    public final boolean canExecute() {
        return canExecute == null || canExecute.canExecute();
    }

    public interface Execute {

        void execute();
    }

    public interface CanExecute {

        boolean canExecute();
    }
}
