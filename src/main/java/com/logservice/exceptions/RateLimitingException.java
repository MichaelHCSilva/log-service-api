package com.logservice.exceptions;

public class RateLimitingException extends RuntimeException {

    private final int statusCode;
    private final String message;

    public RateLimitingException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
