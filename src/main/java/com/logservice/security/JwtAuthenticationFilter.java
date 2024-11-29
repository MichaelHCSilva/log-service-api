package com.logservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.logservice.services.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {

        String authorizationHeader = request.getHeader("Authorization");
        log.info("Cabeçalho de autorização recebido: {}", authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("Cabeçalho de autorização ausente ou inválido");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            log.info("Token validado para usuário: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Autenticação bem-sucedida para: {}", username);
                } else {
                    log.warn("Falha na validação do token");
                }
            }
        } catch (JwtException e) {
            log.error("Erro ao validar o token JWT: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
