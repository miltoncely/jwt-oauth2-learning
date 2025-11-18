package com.learning.auth;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final PrivateKeyLoader privateKeyLoader;

    public JwtService(PrivateKeyLoader privateKeyLoader) {
        this.privateKeyLoader = privateKeyLoader;
    }

    public String generateToken(String username) {
        RSAPrivateKey privateKey = privateKeyLoader.loadPrivateKey();
        long now = System.currentTimeMillis();
        long expiry = now + 3600000; // 1 hour

        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(now))
                .expiration(new Date(expiry))
                .claims(Map.of("role", "USER"))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}
