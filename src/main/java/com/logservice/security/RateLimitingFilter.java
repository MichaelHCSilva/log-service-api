package com.logservice.security;

import java.io.IOException;
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

import com.logservice.config.RateLimitingConfig;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final ConcurrentMap<String, Bucket> globalBuckets = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Bucket> loginBuckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String ipAddress, boolean isLoginRequest) {
        if (isLoginRequest) {
            return loginBuckets.computeIfAbsent(ipAddress, ip -> Bucket.builder()
                    .addLimit(Bandwidth.classic(RateLimitingConfig.MAX_LOGIN_REQUESTS, Refill.intervally(RateLimitingConfig.MAX_LOGIN_REQUESTS, RateLimitingConfig.REFILL_DURATION_LOGIN)))
                    .build());
        } else {
            return globalBuckets.computeIfAbsent(ipAddress, ip -> Bucket.builder()
                    .addLimit(Bandwidth.classic(RateLimitingConfig.MAX_REQUESTS_GLOBAL, Refill.intervally(RateLimitingConfig.MAX_REQUESTS_GLOBAL, RateLimitingConfig.REFILL_DURATION_GLOBAL)))
                    .build());
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        String path = request.getRequestURI();
        boolean isLoginRequest = path.equalsIgnoreCase("/login");

        try {
            log.info("Requisição recebida de IP: {} para o endpoint: {}", clientIp, path);

            Bucket bucket = resolveBucket(clientIp, isLoginRequest);

            if (bucket.tryConsume(1)) {
                log.info("Requisição permitida para IP: {}. Tokens restantes: {}", clientIp, bucket.getAvailableTokens());
                filterChain.doFilter(request, response);
            } else {
                long waitTimeMillis = (isLoginRequest ? RateLimitingConfig.REFILL_DURATION_LOGIN : RateLimitingConfig.REFILL_DURATION_GLOBAL).toMillis();
                log.warn("Limite atingido para IP: {} no endpoint: {}. Tempo de espera: {} ms.", clientIp, path, waitTimeMillis);

                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"erro\": \"Limite de requisições atingido. Aguarde " + waitTimeMillis + " ms.\"}");
                response.getWriter().flush();
            }
        } catch (IOException | ServletException e) {
            log.error("Erro ao processar a requisição de IP: {} para o endpoint: {}. Detalhes: {}", clientIp, path, e.getMessage(), e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Erro interno no servidor.\"}");
            response.getWriter().flush();
        }
    }
}
