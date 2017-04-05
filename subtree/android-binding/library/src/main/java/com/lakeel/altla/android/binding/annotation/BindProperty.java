package com.lakeel.altla.android.binding.annotation;

import com.lakeel.altla.android.binding.BindingMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindProperty {

    int id();

    String name();

    BindingMode mode() default BindingMode.DEFAULT;

    String converter() default "";
}
