package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Converter;
import com.lakeel.altla.android.binding.Property;
import com.lakeel.altla.android.binding.Unbindable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public abstract class AbstractPropertyBinding<TView extends View>
        implements Unbindable {

    private final TView view;

    private final Property<?> property;

    private BindingMode mode = BindingMode.DEFAULT;

    private Converter converter = Converter.EMPTY;

    private boolean targetUpdateSuppressed;

    private boolean sourceUpdateSuppressed;

    private Property.OnValueChangedListener onValueChangedListener;

    protected AbstractPropertyBinding(@NonNull TView view, @NonNull Property<?> property) {
        this.view = view;
        this.property = property;
    }

    @Override
    public final void unbind() {
        if (onValueChangedListener != null) property.removeOnValueChangedListener(onValueChangedListener);
        unbindSource();
    }

    @NonNull
    public final AbstractPropertyBinding<TView> oneWay() {
        checkSourceToTargetBindingSupported();
        mode = BindingMode.ONE_WAY;
        return this;
    }

    @NonNull
    public final AbstractPropertyBinding<TView> twoWay() {
        checkSourceToTargetBindingSupported();
        checkTargetToSourceBindingSupported();
        mode = BindingMode.TWO_WAY;
        return this;
    }

    @NonNull
    public final AbstractPropertyBinding<TView> oneTime() {
        checkSourceToTargetBindingSupported();
        mode = BindingMode.ONE_TIME;
        return this;
    }

    @NonNull
    public final AbstractPropertyBinding<TView> oneWayToSource() {
        checkSourceToTargetBindingSupported();
        mode = BindingMode.ONE_WAY_TO_SOURCE;
        return this;
    }

    @NonNull
    public final AbstractPropertyBinding<TView> converter(@NonNull Converter converter) {
        this.converter = converter;
        return this;
    }

    @NonNull
    public final AbstractPropertyBinding<TView> converter(@Nullable final ConvertDelegate convertDelegate,
                                                          @Nullable final ConvertBackDelegate convertBackDelegate) {
        converter = new Converter() {
            @Override
            public Object convert(Object value) {
                return convertDelegate == null ? value : convertDelegate.convert(value);
            }

            @Override
            public Object convertBack(Object value) {
                return convertBackDelegate == null ? value : convertBackDelegate.convertBack(value);
            }
        };
        return this;
    }

    @NonNull
    public final Unbindable bind() {
        BindingMode mode = (this.mode == BindingMode.DEFAULT) ? getDefaultBindingMode() : this.mode;

        if (mode == BindingMode.ONE_WAY || mode == BindingMode.TWO_WAY || mode == BindingMode.ONE_TIME) {
            updateTarget();
        }

        if (mode == BindingMode.ONE_WAY_TO_SOURCE) {
            updateSource();
        }

        if (mode == BindingMode.ONE_WAY || mode == BindingMode.TWO_WAY) {
            bindTarget();
        }

        if (mode == BindingMode.TWO_WAY || mode == BindingMode.ONE_WAY_TO_SOURCE) {
            bindSource();
        }

        return this;
    }

    @NonNull
    protected TView getView() {
        return view;
    }

    @NonNull
    protected Property<?> getProperty() {
        return property;
    }

    @NonNull
    public Converter getConverter() {
        return converter;
    }

    protected abstract BindingMode getDefaultBindingMode();

    protected abstract boolean isSourceToTargetBindingSupported();

    protected abstract boolean isTargetToSourceBindingSupported();

    protected abstract void updateTargetCore();

    protected abstract void updateSourceCore();

    protected abstract void bindSource();

    protected abstract void unbindSource();

    protected final void updateTarget() {
        sourceUpdateSuppressed = true;
        if (!targetUpdateSuppressed) {
            updateTargetCore();
        }
        sourceUpdateSuppressed = false;
    }

    protected final void updateSource() {
        targetUpdateSuppressed = true;
        if (!sourceUpdateSuppressed) {
            updateSourceCore();
        }
        targetUpdateSuppressed = false;
    }

    private void checkSourceToTargetBindingSupported() {
        if (!isSourceToTargetBindingSupported()) {
            throw new UnsupportedOperationException("Source to target binding not supported.");
        }
    }

    private void checkTargetToSourceBindingSupported() {
        if (!isTargetToSourceBindingSupported()) {
            throw new UnsupportedOperationException("Target to source binding not supported.");
        }
    }

    private void bindTarget() {
        onValueChangedListener = new Property.OnValueChangedListener() {
            @Override
            public void onValueChanged() {
                updateTarget();
            }
        };
        getProperty().addOnValueChangedListener(onValueChangedListener);
    }

    public interface ConvertDelegate {

        Object convert(Object value);
    }

    public interface ConvertBackDelegate {

        Object convertBack(Object value);
    }
}
