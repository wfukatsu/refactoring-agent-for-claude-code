package com.scalar.events_log_tool.application.constant;

public enum LanguageSupported {


    English("en"),
    Japanese("ja");

    private final String code;

    LanguageSupported(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
