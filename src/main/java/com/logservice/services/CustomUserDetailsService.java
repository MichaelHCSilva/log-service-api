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
        validateUsername(username);
        logger.info("carregando usuário: {}", username);

        String password = users.get(username);
        if (password != null) {
            logger.info("Usuário encontrado: {}", username);
            return User.withUsername(username)
                    .password(password)
                    .roles("USER")
                    .build();
        }

        logger.warn("Usuário não encontrado: {}", username);
        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }

    public void registerUser(String username, String password) {
        validateUsername(username);
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
        validateUsername(username);

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
        return false;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("O nome de usuário não pode ser nulo ou vazio.");
        }

        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("O nome de usuário deve ter entre 3 e 20 caracteres.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia.");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }
    }
}
