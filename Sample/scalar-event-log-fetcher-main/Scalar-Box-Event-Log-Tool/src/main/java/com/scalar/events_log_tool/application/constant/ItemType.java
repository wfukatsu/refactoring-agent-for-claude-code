package com.scalar.events_log_tool.application.constant;

public enum ItemType {
    FILE("file"),
    FOLDER("folder");

    private final String type;

    ItemType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
