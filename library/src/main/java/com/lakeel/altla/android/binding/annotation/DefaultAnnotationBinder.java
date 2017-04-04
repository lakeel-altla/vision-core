package com.lakeel.altla.android.binding.annotation;

import com.lakeel.altla.android.binding.AnnotationBinder;
import com.lakeel.altla.android.binding.BinderFactory;
import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Command;
import com.lakeel.altla.android.binding.CommandBinder;
import com.lakeel.altla.android.binding.Converter;
import com.lakeel.altla.android.binding.Property;
import com.lakeel.altla.android.binding.PropertyBinder;
import com.lakeel.altla.android.binding.Unbindable;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultAnnotationBinder implements AnnotationBinder {

    private final Map<String, Converter> converterMap = new HashMap<>();

    private final List<PropertyBinder> propertyBinders = new ArrayList<>();

    private final List<CommandBinder> commandBinders = new ArrayList<>();

    private final BinderFactory binderFactory;

    public DefaultAnnotationBinder(@NonNull BinderFactory binderFactory) {
        this.binderFactory = binderFactory;
    }

    @NonNull
    @Override
    public Unbindable bind() {
        CompositeUnbindable compositeUnbindable = new CompositeUnbindable();

        for (PropertyBinder propertyBinder : propertyBinders) {
            Unbindable unbindable = propertyBinder.bind();
            compositeUnbindable.add(unbindable);
        }

        for (CommandBinder commandBinder : commandBinders) {
            Unbindable unbindable = commandBinder.bind();
            compositeUnbindable.add(unbindable);
        }

        return compositeUnbindable;
    }

    @NonNull
    public AnnotationBinder create(@NonNull Object object) {
        Class<?> clazz = object.getClass();

        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConverterName.class)) {
                    handleConverter(field, object);
                }
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(BindProperty.class)) {
                    handleBindProperty(field, object);
                } else if (field.isAnnotationPresent(BindProperties.class)) {
                    handleBindProperties(field, object);
                } else if (field.isAnnotationPresent(BindCommand.class)) {
                    handleBindCommand(field, object);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    private void handleConverter(@NonNull Field field, @NonNull Object object) throws IllegalAccessException {
        ConverterName converterName = field.getAnnotation(ConverterName.class);
        if (converterName == null) {
            throw new IllegalArgumentException(String.format("Field '%s' has no '%s'.", field, ConverterName.class));
        }

        String name = converterName.value();
        Converter converter = getSourceConverter(field, object);

        converterMap.put(name, converter);
    }

    @NonNull
    private Converter getSourceConverter(@NonNull Field field, @NonNull Object object) throws IllegalAccessException {
        Object fieldValue = field.get(object);
        if (fieldValue == null) {
            throw new IllegalStateException(String.format("'null' field could not be bound: %s", field));
        }
        if (!(fieldValue instanceof Converter)) {
            throw new IllegalStateException(String.format("Field '%s' is not an instance of '%s'",
                                                          field, Converter.class));
        }

        return (Converter) fieldValue;
    }

    private void handleBindProperty(@NonNull Field field, @NonNull Object object) throws IllegalAccessException {
        BindProperty bindProperty = field.getAnnotation(BindProperty.class);
        if (bindProperty == null) {
            throw new IllegalArgumentException(String.format("Field '%s' has no '%s'.", field, BindProperty.class));
        }

        Property<?> source = getSourceProperty(field, object);

        PropertyBinder propertyBinder = createBinder(bindProperty, source);
        propertyBinders.add(propertyBinder);
    }

    private void handleBindProperties(@NonNull Field field, @NonNull Object object) throws IllegalAccessException {
        BindProperties bindProperties = field.getAnnotation(BindProperties.class);
        if (bindProperties == null) {
            throw new IllegalArgumentException(String.format("Field '%s' has no '%s'.", field, BindProperties.class));
        }

        Property<?> source = getSourceProperty(field, object);

        for (BindProperty bindProperty : bindProperties.value()) {
            PropertyBinder propertyBinder = createBinder(bindProperty, source);
            propertyBinders.add(propertyBinder);
        }
    }

    @NonNull
    private Property<?> getSourceProperty(@NonNull Field field, @NonNull Object object) throws IllegalAccessException {
        Object fieldValue = field.get(object);
        if (fieldValue == null) {
            throw new IllegalStateException(String.format("'null' field can not be bound: %s", field));
        }
        if (!(fieldValue instanceof Property<?>)) {
            throw new IllegalStateException(String.format("Field '%s' is not an instance of '%s'",
                                                          field, Property.class));
        }

        return Property.class.cast(fieldValue);
    }

    private PropertyBinder createBinder(@NonNull BindProperty bindProperty, @NonNull Property<?> source) {
        int id = bindProperty.id();
        String propertyName = bindProperty.name();
        BindingMode mode = bindProperty.mode();

        PropertyBinder binder = binderFactory.create(id, propertyName, source)
                                             .mode(mode);

        String converterName = bindProperty.converter();
        if (converterName.length() != 0) {
            Converter converter = converterMap.get(converterName);
            if (converter == null) {
                throw new IllegalStateException(String.format("Converter '%s' not found.", converterName));
            }

            binder.converter(converter);
        }

        return binder;
    }

    private void handleBindCommand(Field field, Object object) throws IllegalAccessException {
        BindCommand bindCommand = field.getAnnotation(BindCommand.class);
        if (bindCommand == null) {
            throw new IllegalArgumentException(String.format("Field '%s' has no '%s'.", field, BindCommand.class));
        }

        Command source = getSourceCommand(field, object);

        CommandBinder commandBinder = createBinder(bindCommand, source);
        commandBinders.add(commandBinder);
    }

    @NonNull
    private Command getSourceCommand(@NonNull Field field, @NonNull Object object) throws IllegalAccessException {
        Object fieldValue = field.get(object);
        if (fieldValue == null) {
            throw new IllegalStateException(String.format("'null' field can not be bound: %s", field));
        }
        if (!(fieldValue instanceof Command)) {
            throw new IllegalStateException(String.format("Field '%s' is not an instance of '%s'",
                                                          field, Command.class));
        }

        return Command.class.cast(fieldValue);
    }

    @NonNull
    private CommandBinder createBinder(BindCommand bindCommand, Command source) {
        int id = bindCommand.id();
        String commandName = bindCommand.name();

        return binderFactory.create(id, commandName, source);
    }

    private final class CompositeUnbindable implements Unbindable {

        private final List<Unbindable> unbindables = new ArrayList<>();

        public void add(@NonNull Unbindable unbindable) {
            unbindables.add(unbindable);
        }

        @Override
        public void unbind() {
            for (Unbindable unbindable : unbindables) {
                unbindable.unbind();
            }
        }
    }
}
