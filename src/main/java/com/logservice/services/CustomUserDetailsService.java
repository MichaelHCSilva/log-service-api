package com.logservice.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private static final int MAX_ATTEMPTS = 5;
    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        validateField(username, "Usuário", 3, 50);
        logger.info("Carregando usuário: {}", username);

        String password = users.get(username);
        if (password == null) {
            logger.warn("Usuário não encontrado: {}", username);
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }

        logger.info("Usuário encontrado: {}", username);
        return User.withUsername(username)
                .password(password)
                .roles("USER")
                .build();
    }

    public void registerUser(String username, String password) {
        validateField(username, "Usuário", 3, 50);
        validatePassword(password);

        logger.info("Registrando novo usuário: {}", username);

        if (users.containsKey(username)) {
            logger.error("Usuário já existe: {}", username);
            throw new IllegalArgumentException("Usuário já existe!");
        }

        String encodedPassword = passwordEncoder.encode(password);
        users.put(username, encodedPassword);
        logger.info("Usuário registrado com sucesso: {}, senha criptografada: {}", username, encodedPassword);
    }

    public boolean authenticateUser(String username, String password) {
        validateField(username, "Usuário", 3, 50);
        validateField(password, "Senha", 6, 100);

        AtomicInteger attempts = loginAttempts.computeIfAbsent(username, k -> new AtomicInteger(0));
        if (attempts.get() >= MAX_ATTEMPTS) {
            logger.warn("Usuário bloqueado por tentativas excessivas: {}", username);
            throw new IllegalStateException("Usuário bloqueado. Tente novamente mais tarde.");
        }

        logger.info("Autenticando usuário: {}", username);
        String encodedPassword = users.get(username);

        if (encodedPassword != null && passwordEncoder.matches(password, encodedPassword)) {
            logger.info("Autenticação bem-sucedida para usuário: {}", username);
            loginAttempts.remove(username);
            return true;
        }

        logger.warn("Falha na autenticação para usuário: {}", username);
        attempts.incrementAndGet();
        throw new IllegalArgumentException("Usuário ou senha incorretos.");
    }

    private void validateField(String field, String fieldName, int minLength, int maxLength) {
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException(fieldName + " não pode ser estar vazio.");
        
        }
        if (field.length() < minLength) {
            throw new IllegalArgumentException(fieldName + " deve ter pelo menos " + minLength + " caracteres.");
        }

        if (field.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " não pode ter mais que " + maxLength + " caracteres.");
        }
    }

    private void validatePassword(String password) {
        validateField(password, "Senha", 6, 100);
        if (!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{6,}$")) {
            throw new IllegalArgumentException("A senha deve conter pelo menos uma letra maiúscula, um número e um caractere especial.");
        }
    }
}
