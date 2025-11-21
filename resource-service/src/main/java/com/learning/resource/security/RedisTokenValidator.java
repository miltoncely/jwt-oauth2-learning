package com.learning.resource.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RedisTokenValidator {

    private final ReactiveRedisTemplate<String, String> activeTokensCache;

    public RedisTokenValidator(@Qualifier("activeTokensCache") ReactiveRedisTemplate<String, String> activeTokensCache) {
        this.activeTokensCache = activeTokensCache;
    }

    public Mono<Boolean> isTokenInWhitelist(String token) {
        return activeTokensCache.hasKey(token);
    }

    public Mono<Boolean> isTokenActive(String token) {
        return isTokenInWhitelist(token)
            .defaultIfEmpty(false); 
    }
}
