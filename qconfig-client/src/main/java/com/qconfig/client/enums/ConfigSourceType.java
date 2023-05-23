package com.qconfig.client.enums;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/15:20
 */
public enum ConfigSourceType {
    REMOTE("Loaded from remote config service"), LOCAL("Loaded from local cache"), NONE("Load failed");

    private final String description;

    ConfigSourceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
