package com.lakeel.altla.android.binding;

public enum CommandName {
    ON_CLICK("onClick"),
    ON_LONG_CLICK("onLongClick");

    private String value;

    CommandName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
