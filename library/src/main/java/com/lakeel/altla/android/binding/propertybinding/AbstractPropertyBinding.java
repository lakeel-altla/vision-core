package com.lakeel.altla.android.binding.propertybinding;

import com.lakeel.altla.android.binding.BindingMode;
import com.lakeel.altla.android.binding.Property;
import com.lakeel.altla.android.binding.Unbindable;

import android.support.annotation.NonNull;
import android.view.View;

public abstract class AbstractPropertyBinding<TView extends View, TProperty extends Property>
        implements Unbindable {

    private final TView view;

    private final TProperty property;

    private BindingMode mode = BindingMode.DEFAULT;

    private boolean targetUpdateSuppressed;

    private boolean sourceUpdateSuppressed;

    private Property.OnValueChangedListener onValueChangedListener;

    protected AbstractPropertyBinding(@NonNull TView view, @NonNull TProperty property) {
        this.view = view;
        this.property = property;
    }

    @Override
    public final void unbind() {
        if (onValueChangedListener != null) property.removeOnValueChangedListener(onValueChangedListener);
        unbindSource();
    }

    @NonNull
    public final AbstractPropertyBinding<TView, TProperty> oneWay() {
        checkSourceToTargetBindingSupported();
        mode = BindingMode.ONE_WAY;
        return this;
    }

    @NonNull
    public final AbstractPropertyBinding<TView, TProperty> twoWay() {
        checkSourceToTargetBindingSupported();
        checkTargetToSourceBindingSupported();
        mode = BindingMode.TWO_WAY;
        return this;
    }

    @NonNull
    public final AbstractPropertyBinding<TView, TProperty> oneTime() {
        checkSourceToTargetBindingSupported();
        mode = BindingMode.ONE_TIME;
        return this;
    }

    @NonNull
    public final AbstractPropertyBinding<TView, TProperty> oneWayToSource() {
        checkSourceToTargetBindingSupported();
        mode = BindingMode.ONE_WAY_TO_SOURCE;
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
    protected TProperty getProperty() {
        return property;
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
        if (!isSourceToTargetBindingSupported())
            throw new UnsupportedOperationException("Source to target binding not supported.");
    }

    private void checkTargetToSourceBindingSupported() {
        if (!isTargetToSourceBindingSupported())
            throw new UnsupportedOperationException("Target to source binding not supported.");
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
}
