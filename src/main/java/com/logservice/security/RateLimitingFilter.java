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

    private static final long MAX_REQUESTS = 5; // Número máximo de requisições permitidas por intervalo
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1); // Tempo para renovar os tokens
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Resolve o bucket associado ao endereço IP. Se não existir, cria um novo.
     */
    private Bucket resolveBucket(String ipAddress) {
        return buckets.computeIfAbsent(ipAddress, k -> 
            Bucket.builder()
                    .addLimit(Bandwidth.classic(MAX_REQUESTS, Refill.intervally(MAX_REQUESTS, REFILL_DURATION)))
                    .build()
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {

        String clientIp = request.getRemoteAddr(); // Obtém o endereço IP do cliente
        log.info("Requisição recebida de IP: {}", clientIp);

        Bucket bucket = resolveBucket(clientIp);

        // Verifica se ainda há tokens disponíveis no bucket
        if (bucket.tryConsume(1)) {
            long remainingTokens = bucket.getAvailableTokens();
            log.info("Requisição permitida para IP: {}. Requisições restantes: {}", clientIp, remainingTokens);
            filterChain.doFilter(request, response);
        } else {
            long waitTimeMillis = REFILL_DURATION.toMillis();
            log.warn("Limite de requisições atingido para IP: {}. Tempo de espera estimado: {} ms.", clientIp, waitTimeMillis);

            // Configura a resposta HTTP para indicar limite atingido
            response.setStatus(429); // Status HTTP "Too Many Requests"
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Limite de requisições atingido. Por favor, aguarde " + waitTimeMillis + " ms antes de tentar novamente.\"}");
            response.getWriter().flush();
        }
    }
}
