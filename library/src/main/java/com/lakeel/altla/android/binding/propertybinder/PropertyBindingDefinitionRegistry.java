package com.lakeel.altla.android.binding.propertybinder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class PropertyBindingDefinitionRegistry {

    private final Map<Class<?>, Map<String, PropertyBindingDefinition>> definitionMap = new HashMap<>();

    public void register(@NonNull PropertyBindingDefinition definition) {
        Map<String, PropertyBindingDefinition> map = definitionMap.get(definition.getViewType());
        if (map == null) {
            map = new HashMap<>();
            definitionMap.put(definition.getViewType(), map);
        }
        map.put(definition.getPropertyName(), definition);
    }

    public void deregister(@NonNull PropertyBindingDefinition definition) {
        Map<String, PropertyBindingDefinition> map = definitionMap.get(definition.getViewType());
        if (map != null) {
            map.remove(definition.getPropertyName());
        }
    }

    @Nullable
    public PropertyBindingDefinition find(@NonNull Class<?> viewType, @NonNull String propertyName) {
        PropertyBindingDefinition definition = null;

        Map<String, PropertyBindingDefinition> map = definitionMap.get(viewType);
        if (map != null) {
            definition = map.get(propertyName);
        }

        if (definition == null) {
            Class<?> superclass = viewType.getSuperclass();
            return superclass == null ? null : find(superclass, propertyName);
        } else {
            return definition;
        }
    }
}
