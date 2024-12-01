package com.logservice.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParsingException(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        Throwable cause = ex.getCause();

        if (cause != null) {
            switch (cause) {
                case InvalidFormatException invalidFormatException -> {
                    String fieldName = invalidFormatException.getPath().isEmpty()
                            ? "unknown"
                            : invalidFormatException.getPath().get(0).getFieldName();

                    if ("level".equals(fieldName)) {
                        error.put("error", "O nível de log fornecido é inválido.");
                        error.put("details", "Use um dos valores aceitos: ERROR, DEBUG, WARNING, INFO.");
                    } else {
                        error.put("error", "Erro de formato inválido no campo: " + fieldName);
                        error.put("details", invalidFormatException.getOriginalMessage());
                    }
                }
                case ValueInstantiationException valueInstantiationException -> {
                    String fieldName = valueInstantiationException.getPath().isEmpty()
                            ? "unknown"
                            : valueInstantiationException.getPath().get(0).getFieldName();

                    error.put("error", "Erro ao processar valor para o campo: " + fieldName);
                    error.put("details", "O campo '" + fieldName + "' pode estar vazio ou inválido.");
                }
                default -> {
                    error.put("error", "Erro ao processar o JSON enviado.");
                    error.put("details", cause.getMessage());
                }
            }
        } else {
            error.put("error", "Erro ao processar o JSON enviado.");
            error.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Erro interno no servidor.");
        error.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
