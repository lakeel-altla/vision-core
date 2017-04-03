package com.lakeel.altla.android.binding;

import com.lakeel.altla.android.binding.target.CompoundButtonTarget;
import com.lakeel.altla.android.binding.target.EditTextTarget;
import com.lakeel.altla.android.binding.target.RadioGroupTarget;
import com.lakeel.altla.android.binding.target.TextViewTarget;
import com.lakeel.altla.android.binding.target.ViewTarget;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public final class Binder {

    private final Activity activity;

    private final View container;

    public Binder(@NonNull Activity activity) {
        this.activity = activity;
        container = null;
    }

    public Binder(@NonNull View container) {
        activity = null;
        this.container = container;
    }

    @NonNull
    public ViewTarget view(@IdRes int id) {
        View view = findById(id);
        return view(view);
    }

    @NonNull
    public static ViewTarget view(@NonNull View view) {
        return new ViewTarget(view);
    }

    @NonNull
    public TextViewTarget textView(@IdRes int id) {
        TextView textView = findById(id);
        return textView(textView);
    }

    @NonNull
    public TextViewTarget textView(@NonNull TextView textView) {
        return new TextViewTarget(textView);
    }

    @NonNull
    public EditTextTarget editText(@IdRes int id) {
        EditText editText = findById(id);
        return editText(editText);
    }

    @NonNull
    public EditTextTarget editText(@NonNull EditText editText) {
        return new EditTextTarget(editText);
    }

    @NonNull
    public RadioGroupTarget radioGroup(@IdRes int id) {
        RadioGroup radioGroup = findById(id);
        return radioGroup(radioGroup);
    }

    @NonNull
    public RadioGroupTarget radioGroup(@NonNull RadioGroup radioGroup) {
        return new RadioGroupTarget(radioGroup);
    }

    @NonNull
    public CompoundButtonTarget compoundButton(@IdRes int id) {
        CompoundButton compoundButton = findById(id);
        return compoundButton(compoundButton);
    }

    @NonNull
    public CompoundButtonTarget compoundButton(@NonNull CompoundButton compoundButton) {
        return new CompoundButtonTarget(compoundButton);
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T findById(@IdRes int id) {

        T view;
        if (activity != null) {
            view = (T) activity.findViewById(id);
        } else if (container != null) {
            view = (T) container.findViewById(id);
        } else {
            throw new IllegalStateException("View container not found.");
        }

        throwExceptionIfViewIsNull(view);
        return view;
    }

    private static void throwExceptionIfViewIsNull(@Nullable View view) {
        if (view == null) {
            throw new IllegalArgumentException("View not found.");
        }
    }
}
