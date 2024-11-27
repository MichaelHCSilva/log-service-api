package com.logservice.services;

import java.util.concurrent.ConcurrentHashMap;

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

    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //public CustomUserDetailsService() {
    //    String encodedPassword = passwordEncoder.encode("password");
    //    users.put("admin", encodedPassword);
    //    logger.info("Usuário inicial criado: admin, senha criptografada: {}", encodedPassword);
    //}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Tentando carregar o usuário: {}", username);

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
        logger.info("Autenticando usuário: {}", username);
        String encodedPassword = users.get(username);

        if (encodedPassword != null && passwordEncoder.matches(password, encodedPassword)) {
            logger.info("Autenticação bem-sucedida para usuário: {}", username);
            return true;
        }

        logger.warn("Falha na autenticação para usuário: {}", username);
        return false;
    }
}
