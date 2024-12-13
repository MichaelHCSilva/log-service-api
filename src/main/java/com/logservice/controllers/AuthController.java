package com.logservice.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logservice.requests.AuthRequest;
import com.logservice.services.CustomUserDetailsService;
import com.logservice.services.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(CustomUserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody AuthRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            userDetailsService.registerUser(request.getUsername(), request.getPassword());
            response.put("message", "Usuário registrado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody AuthRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Autenticação do usuário
            boolean isAuthenticated = userDetailsService.authenticateUser(request.getUsername(), request.getPassword());

            if (isAuthenticated) {
                String token = jwtService.generateToken(request.getUsername());
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Usuário não encontrado ou credenciais inválidas.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("error", "Erro ao realizar login. Tente novamente.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
