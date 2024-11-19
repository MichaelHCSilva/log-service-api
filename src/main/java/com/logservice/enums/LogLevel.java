package com.logservice.enums;

public enum LogLevel {
    INFO("Informational message"),
    WARNING("Potential issue or important notice"),
    ERROR("An error occurred"),
    DEBUG("Debugging message");

    private final String description;

    LogLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
