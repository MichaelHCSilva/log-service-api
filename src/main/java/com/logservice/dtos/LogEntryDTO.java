package com.logservice.dtos;

import java.util.Map;

import com.logservice.enums.LogLevel;

import jakarta.validation.constraints.NotNull;


public class LogEntryDTO {

    @NotNull(message = "Log level is required")
    private LogLevel level;

    @NotNull(message = "Message is required")
    private String message;

    private Map<String, Object> additionalData;

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }
}