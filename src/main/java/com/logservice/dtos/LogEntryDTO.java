package com.logservice.dtos;

import com.logservice.enums.LogLevel;

import jakarta.validation.constraints.NotBlank;

public class LogEntryDTO {

    @NotBlank(message = "Log level is required")
    private LogLevel level;

    @NotBlank(message = "Message is required")
    private String message;

    private String additionalData;

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

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }
}