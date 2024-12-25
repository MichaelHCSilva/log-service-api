package com.logservice.security;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    // Configurações para endpoints gerais
    private static final long MAX_REQUESTS_GLOBAL = Long.parseLong(System.getenv("MAX_REQUESTS_GLOBAL"));
    private static final Duration REFILL_DURATION_GLOBAL = Duration.ofMinutes(Long.parseLong(System.getenv("REFILL_DURATION_MINUTES_GLOBAL")));

    // Configurações específicas para login
    private static final long MAX_LOGIN_REQUESTS = Long.parseLong(System.getenv("MAX_LOGIN_REQUESTS"));
    private static final Duration REFILL_DURATION_LOGIN = Duration.ofMinutes(Long.parseLong(System.getenv("REFILL_DURATION_MINUTES_LOGIN")));

    // Mapas para armazenar os buckets
    private final ConcurrentMap<String, Bucket> globalBuckets = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Bucket> loginBuckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String ipAddress, boolean isLoginRequest) {
        if (isLoginRequest) {
            return loginBuckets.computeIfAbsent(ipAddress, ip -> Bucket.builder()
                    .addLimit(Bandwidth.classic(MAX_LOGIN_REQUESTS, Refill.intervally(MAX_LOGIN_REQUESTS, REFILL_DURATION_LOGIN)))
                    .build());
        } else {
            return globalBuckets.computeIfAbsent(ipAddress, ip -> Bucket.builder()
                    .addLimit(Bandwidth.classic(MAX_REQUESTS_GLOBAL, Refill.intervally(MAX_REQUESTS_GLOBAL, REFILL_DURATION_GLOBAL)))
                    .build());
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {

        String clientIp = request.getRemoteAddr();
        String path = request.getRequestURI();
        boolean isLoginRequest = path.equalsIgnoreCase("/login"); // Verifica se é requisição de login

        log.info("Requisição recebida de IP: {} para o endpoint: {}", clientIp, path);

        Bucket bucket = resolveBucket(clientIp, isLoginRequest);

        if (bucket.tryConsume(1)) {
            log.info("Requisição permitida para IP: {}. Tokens restantes: {}", clientIp, bucket.getAvailableTokens());
            filterChain.doFilter(request, response);
        } else {
            long waitTimeMillis = (isLoginRequest ? REFILL_DURATION_LOGIN : REFILL_DURATION_GLOBAL).toMillis();
            log.warn("Limite atingido para IP: {} no endpoint: {}. Tempo de espera: {} ms.", clientIp, path, waitTimeMillis);

            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Limite de requisições atingido. Aguarde " + waitTimeMillis + " ms.\"}");
            response.getWriter().flush();
        }
    }
}
