package com.logservice.dtos;

import java.util.Map;

import com.logservice.enums.LogNivel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class LogEntryDTO {

    @NotNull(message = "O nível de log é obrigatório.")
    private LogNivel nivel;

    @NotBlank(message = "A mensagem de log é obrigatório.")
    private String message;

    private Map<String, Object> additionalData;

    public LogNivel getNivel() {
        return nivel;
    }

    public void setNivel(LogNivel nivel) {
        this.nivel = nivel;
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