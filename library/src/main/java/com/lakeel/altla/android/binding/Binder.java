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

    private Binder() {
    }

    @NonNull
    public static ViewTarget view(@NonNull Activity activity, @IdRes int id) {
        View view = findById(activity, id);
        return view(view);
    }

    @NonNull
    public static ViewTarget view(@NonNull View parent, @IdRes int id) {
        View view = findById(parent, id);
        return view(view);
    }

    @NonNull
    public static ViewTarget view(@NonNull View view) {
        return new ViewTarget(view);
    }

    @NonNull
    public static TextViewTarget textView(@NonNull Activity activity, @IdRes int id) {
        TextView textView = findById(activity, id);
        return textView(textView);
    }

    @NonNull
    public static TextViewTarget textView(@NonNull View parent, @IdRes int id) {
        TextView textView = findById(parent, id);
        return textView(textView);
    }

    @NonNull
    public static TextViewTarget textView(@NonNull TextView textView) {
        return new TextViewTarget(textView);
    }

    @NonNull
    public static EditTextTarget editText(@NonNull Activity activity, @IdRes int id) {
        EditText editText = findById(activity, id);
        return editText(editText);
    }

    @NonNull
    public static EditTextTarget editText(@NonNull View parent, @IdRes int id) {
        EditText editText = findById(parent, id);
        return editText(editText);
    }

    @NonNull
    public static EditTextTarget editText(@NonNull EditText editText) {
        return new EditTextTarget(editText);
    }

    @NonNull
    public static RadioGroupTarget radioGroup(@NonNull Activity activity, @IdRes int id) {
        RadioGroup radioGroup = findById(activity, id);
        return radioGroup(radioGroup);
    }

    @NonNull
    public static RadioGroupTarget radioGroup(@NonNull View parent, @IdRes int id) {
        RadioGroup radioGroup = findById(parent, id);
        return radioGroup(radioGroup);
    }

    @NonNull
    public static RadioGroupTarget radioGroup(@NonNull RadioGroup radioGroup) {
        return new RadioGroupTarget(radioGroup);
    }

    @NonNull
    public static CompoundButtonTarget compoundButton(@NonNull Activity activity, @IdRes int id) {
        CompoundButton compoundButton = findById(activity, id);
        return compoundButton(compoundButton);
    }

    @NonNull
    public static CompoundButtonTarget compoundButton(@NonNull View parent, @IdRes int id) {
        CompoundButton compoundButton = findById(parent, id);
        return compoundButton(compoundButton);
    }

    @NonNull
    public static CompoundButtonTarget compoundButton(@NonNull CompoundButton compoundButton) {
        return new CompoundButtonTarget(compoundButton);
    }

    @SuppressWarnings("unchecked")
    private static <T extends View> T findById(@NonNull Activity activity, @IdRes int id) {
        T view = (T) activity.findViewById(id);
        throwExceptionIfViewIsNull(view);
        return view;
    }

    @SuppressWarnings("unchecked")
    private static <T extends View> T findById(@NonNull View parent, @IdRes int id) {
        T view = (T) parent.findViewById(id);
        throwExceptionIfViewIsNull(view);
        return view;
    }

    private static void throwExceptionIfViewIsNull(@Nullable View view) {
        if (view == null) {
            throw new IllegalArgumentException("View not found.");
        }
    }
}
