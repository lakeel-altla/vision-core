package com.lakeel.altla.android.binding;

public enum PropertyName {
    ENABLED("enabled"),
    TEXT("text"),
    CHECKED("checked"),
    CHECKED_BUTTON("checkedButton");

    private String value;

    PropertyName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
