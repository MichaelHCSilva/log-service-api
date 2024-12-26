package com.logservice.responses;

import java.util.regex.Pattern;

public class AuthResponse {

    private final String token;

    private static final String JWT_REGEX = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$";

    public AuthResponse(String token) {
        this.token = validateToken(token);
    }

    private String validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token não pode ser vazio ou nulo.");
            }

            if (!Pattern.matches(JWT_REGEX, token)) {
                throw new IllegalArgumentException("Formato de token inválido.");
            }

            return token;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public String getToken() {
        return token;
    }
}
