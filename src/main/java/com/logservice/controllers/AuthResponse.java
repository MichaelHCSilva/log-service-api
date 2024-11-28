package com.logservice.controllers;

import java.util.regex.Pattern;

public class AuthResponse {
    private final String token;

    private static final String JWT_REGEX = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$";  

    public AuthResponse(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token não pode ser vazio ou nulo.");
        }

        if (!Pattern.matches(JWT_REGEX, token)) {
            throw new IllegalArgumentException("Formato de token inválido.");
        }

        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

