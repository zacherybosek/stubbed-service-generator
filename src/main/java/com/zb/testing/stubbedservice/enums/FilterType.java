package com.zb.testing.stubbedservice.enums;

/**
 * Created by Zachery on 7/24/2016.
 */
public enum FilterType {
    ANY("any"),
    NONE("none"),
    CUSTOM("custom");

    private String value;

    FilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
