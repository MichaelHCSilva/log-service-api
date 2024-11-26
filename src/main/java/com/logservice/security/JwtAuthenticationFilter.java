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
    log.info("Authorization header: {}", authorizationHeader);

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        logger.warn("Authorization header missing or invalid");
        filterChain.doFilter(request, response);
        return;
    }

    String token = authorizationHeader.substring(7);
    log.info("Extracted token: {}", token);

    try {
        String username = jwtService.extractUsername(token);
        log.info("Extracted username: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.info("Loaded UserDetails for: {}", username);

            if (jwtService.validateToken(token, userDetails.getUsername())) {
                log.info("Token validated successfully for: {}", username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                log.warn("Token validation failed for: {}", username);
            }
        }
    } catch (JwtException e) {
        log.error("JWT validation failed: {}", e.getMessage(), e);
    }

    filterChain.doFilter(request, response);
}

}
