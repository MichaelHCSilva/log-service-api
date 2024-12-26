package com.logservice.config;

import java.time.Duration;

public class RateLimitingConfig {
    // Configurações gerais (poderiam vir de um arquivo de propriedades)
    public static final long MAX_REQUESTS_GLOBAL = Long.parseLong(System.getenv("MAX_REQUESTS_GLOBAL"));
    public static final Duration REFILL_DURATION_GLOBAL = Duration.ofMinutes(Long.parseLong(System.getenv("REFILL_DURATION_MINUTES_GLOBAL")));

    // Configurações específicas para login
    public static final long MAX_LOGIN_REQUESTS = Long.parseLong(System.getenv("MAX_LOGIN_REQUESTS"));
    public static final Duration REFILL_DURATION_LOGIN = Duration.ofMinutes(Long.parseLong(System.getenv("REFILL_DURATION_MINUTES_LOGIN")));
}
