package com.lakeel.altla.android.binding.commandbinder;

import com.lakeel.altla.android.binding.Command;
import com.lakeel.altla.android.binding.CommandBinder;

import android.support.annotation.NonNull;
import android.view.View;

import java.lang.reflect.Constructor;

public final class CommandBindingDefinition {

    private final Class<? extends View> viewType;

    private final String commandName;

    private final Class<? extends CommandBinder> binderType;

    private final Constructor<? extends CommandBinder> binderConstructor;

    public CommandBindingDefinition(@NonNull Class<? extends View> viewType, @NonNull String commandName,
                                    @NonNull Class<? extends CommandBinder> binderType)
            throws NoSuchMethodException {
        this.viewType = viewType;
        this.commandName = commandName;
        this.binderType = binderType;

        binderConstructor = binderType.getConstructor(CommandBindingDefinition.class, View.class, Command.class);
    }

    @NonNull
    public Class<? extends View> getViewType() {
        return viewType;
    }

    @NonNull
    public String getCommandName() {
        return commandName;
    }

    @NonNull
    public Class<? extends CommandBinder> getBinderType() {
        return binderType;
    }

    @NonNull
    public Constructor<? extends CommandBinder> getBinderConstructor() {
        return binderConstructor;
    }
}
