package com.logservice.enums;

public enum LogNivel {
    INFO("Mensagem informativa"),
    WARNING("Aviso sobre um possível problema ou nota importante"),
    ERROR("Um erro ocorreu"),
    DEBUG("Mensagem de depuração");

    private final String description;

    LogNivel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
