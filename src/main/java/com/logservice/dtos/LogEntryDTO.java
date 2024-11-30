package com.logservice.dtos;

import java.util.Map;

import com.logservice.enums.LogLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class LogEntryDTO {

    @NotNull(message = "O nível de log é obrigatório.")
    private LogLevel level;

    @NotBlank(message = "A mensagem de log é obrigatória e não pode estar em branco.")
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