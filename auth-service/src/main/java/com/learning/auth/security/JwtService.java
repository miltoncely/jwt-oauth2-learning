package com.learning.auth.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final PrivateKeyLoader privateKeyLoader;

    private final ReactiveRedisTemplate<String, String> activeTokensCache;

    public JwtService(PrivateKeyLoader privateKeyLoader,
                      @Qualifier("activeTokensCache") ReactiveRedisTemplate<String, String> activeTokensCache) {
        this.privateKeyLoader = privateKeyLoader;
        this.activeTokensCache = activeTokensCache;
    }

    public String generateToken(String username, String roles) {
        RSAPrivateKey privateKey = privateKeyLoader.loadPrivateKey();
        long now = System.currentTimeMillis();
        long expiry = now + 3600000; // 1 hour

        String token = Jwts.builder()
                .header().add("typ", "JWT").and()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(now))
                .expiration(new Date(expiry))
                .claims(Map.of("roles", roles.split(",")))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();

        // Store token in Redis with 1 hour TTL (fire and forget style for this demo, or block if strict)
        activeTokensCache.opsForValue().set(token, username, Duration.ofHours(1)).subscribe();

        return token;
    }

    public reactor.core.publisher.Mono<Boolean> revokeToken(String token) {
        return activeTokensCache.opsForValue().delete(token);
    }
}
