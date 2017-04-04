package com.lakeel.altla.android.binding.propertybinder;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Property;
import com.lakeel.altla.android.binding.PropertyBinder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class PropertyBindingDefinition {

    private final Class<? extends View> viewType;

    private final String propertyName;

    private final Class<?> propertyType;

    private final String getterName;

    private final String setterName;

    private final BindingMode defaultBindingMode;

    private final Class<? extends PropertyBinder> binderType;

    private final Method getterMethod;

    private final Method setterMethod;

    private final Constructor<? extends PropertyBinder> binderConstructor;

    public PropertyBindingDefinition(@NonNull Class<? extends View> viewType,
                                     @NonNull String propertyName,
                                     @NonNull Class<?> propertyType,
                                     @Nullable String getterName,
                                     @Nullable String setterName,
                                     @NonNull BindingMode defaultBindingMode,
                                     @NonNull Class<? extends PropertyBinder> binderType)
            throws NoSuchMethodException {
        this.viewType = viewType;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.getterName = getterName;
        this.setterName = setterName;
        this.defaultBindingMode = defaultBindingMode;
        this.binderType = binderType;

        getterMethod = getterName == null ? null : viewType.getMethod(getterName);
        setterMethod = setterName == null ? null : viewType.getMethod(setterName, propertyType);

        binderConstructor = binderType.getConstructor(PropertyBindingDefinition.class, View.class, Property.class);
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
    public Class<?> getPropertyType() {
        return propertyType;
    }

    @Nullable
    public String getGetterName() {
        return getterName;
    }

    @Nullable
    public String getSetterName() {
        return setterName;
    }

    @NonNull
    public BindingMode getDefaultBindingMode() {
        return defaultBindingMode;
    }

    @NonNull
    public Class<? extends PropertyBinder> getBinderType() {
        return binderType;
    }

    @Nullable
    public Method getGetterMethod() {
        return getterMethod;
    }

    @Nullable
    public Method getSetterMethod() {
        return setterMethod;
    }

    @NonNull
    public Constructor<? extends PropertyBinder> getBinderConstructor() {
        return binderConstructor;
    }
}
