package com.lakeel.altla.android.binding.propertybinder;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Property;
import com.lakeel.altla.android.binding.PropertyBinder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class PropertyBindingDefinition {

    private static final String TAG = PropertyBindingDefinition.class.getSimpleName();

    private final Class<? extends View> viewType;

    private final String propertyName;

    private final BindingMode defaultBindingMode;

    private final Method readMethod;

    private final Method writeMethod;

    private final Constructor<? extends PropertyBinder> binderConstructor;

    public PropertyBindingDefinition(@NonNull Class<? extends View> viewType,
                                     @NonNull String propertyName,
                                     @Nullable Method readMethod,
                                     @Nullable Method writeMethod,
                                     @NonNull BindingMode defaultBindingMode,
                                     @NonNull Class<? extends PropertyBinder> binderType)
            throws NoSuchMethodException {

        this.viewType = viewType;
        this.propertyName = propertyName;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.defaultBindingMode = defaultBindingMode;

        binderConstructor = binderType.getConstructor(PropertyBindingDefinition.class, View.class, Property.class);
    }

    @NonNull
    public static PropertyBindingDefinition create(@NonNull Class<? extends View> viewType,
                                                   @NonNull String propertyName,
                                                   @NonNull BindingMode defaultBindingMode,
                                                   @NonNull Class<? extends PropertyBinder> binderType)
            throws NoSuchMethodException {

        Method readMethod = null;
        String readMethodName = PropertyNameResolver.resolveReadMethodNameByNonBoolean(propertyName);

        try {
            readMethod = viewType.getMethod(readMethodName);
        } catch (NoSuchMethodException e1) {
            readMethodName = PropertyNameResolver.resolveReadMethodNameByBoolean(propertyName);
            try {
                readMethod = viewType.getMethod(readMethodName);
            } catch (NoSuchMethodException e2) {
                Log.d(TAG, String.format("The read method not found: viewType = %s, propertyName = %s",
                                         viewType, propertyName));
            }
        }

        Method writeMethod = null;

        if (readMethod == null) {
            Log.d(TAG, String.format("The write method can not be resolved: viewType = %s, propertyName = %s",
                                     viewType, propertyName));
        } else {
            Class<?> propertyType = readMethod.getReturnType();
            String writeMethodName = PropertyNameResolver.resolveWriteMethodName(propertyName);

            try {
                writeMethod = viewType.getMethod(writeMethodName, propertyType);
            } catch (NoSuchMethodException e) {
                Log.d(TAG, String.format(
                        "The write method not found: viewType = %s, propertyName = %s, propertyType = %s",
                        viewType, propertyName, propertyType));
            }
        }

        return new PropertyBindingDefinition(viewType, propertyName, readMethod, writeMethod, defaultBindingMode,
                                             binderType);
    }

    @NonNull
    public static PropertyBindingDefinition create(@NonNull Class<? extends View> viewType,
                                                   @NonNull String propertyName,
                                                   @NonNull Class<?> propertyType,
                                                   @NonNull BindingMode defaultBindingMode,
                                                   @NonNull Class<? extends PropertyBinder> binderType)
            throws NoSuchMethodException {

        Method readMethod = null;
        String readMethodName = PropertyNameResolver.resolveReadMethodName(propertyName, propertyType);

        try {
            readMethod = viewType.getMethod(readMethodName);
        } catch (NoSuchMethodException e) {
            Log.d(TAG, String.format("The read method not found: viewType = %s, propertyName = %s, propertyType = %s",
                                     viewType, propertyName, propertyType));
        }

        Method writeMethod = null;
        String writeMethodName = PropertyNameResolver.resolveWriteMethodName(propertyName);

        try {
            writeMethod = viewType.getMethod(writeMethodName, propertyType);
        } catch (NoSuchMethodException e) {
            Log.d(TAG, String.format("The write method not found: viewType = %s, propertyName = %s, propertyType = %s",
                                     viewType, propertyName, propertyType));
        }

        return new PropertyBindingDefinition(viewType, propertyName, readMethod, writeMethod, defaultBindingMode,
                                             binderType);
    }

    @NonNull
    public static PropertyBindingDefinition create(@NonNull Class<? extends View> viewType,
                                                   @NonNull String propertyName,
                                                   @NonNull Class<?> propertyType,
                                                   @Nullable String readMethodName,
                                                   @Nullable String writeMethodName,
                                                   @NonNull BindingMode defaultBindingMode,
                                                   @NonNull Class<? extends PropertyBinder> binderType)
            throws NoSuchMethodException {

        Method readMethod = readMethodName == null ? null : viewType.getMethod(readMethodName);
        Method writeMethod = writeMethodName == null ? null : viewType.getMethod(writeMethodName, propertyType);

        return new PropertyBindingDefinition(viewType, propertyName, readMethod, writeMethod, defaultBindingMode,
                                             binderType);
    }

    @NonNull
    public Class<? extends View> getViewType() {
        return viewType;
    }

    @NonNull
    public String getPropertyName() {
        return propertyName;
    }

    @NonNull
    public BindingMode getDefaultBindingMode() {
        return defaultBindingMode;
    }

    @Nullable
    public Method getReadMethod() {
        return readMethod;
    }

    @Nullable
    public Method getWriteMethod() {
        return writeMethod;
    }

    @NonNull
    public Constructor<? extends PropertyBinder> getBinderConstructor() {
        return binderConstructor;
    }
}
