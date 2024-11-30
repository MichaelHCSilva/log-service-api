package com.logservice.enums;

public enum LogLevel {
    INFO("Mensagem informativa"),
    WARNING("Aviso sobre um possível problema ou nota importante"),
    ERROR("Um erro ocorreu"),
    DEBUG("Mensagem de depuração");

    private final String description;

    LogLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
