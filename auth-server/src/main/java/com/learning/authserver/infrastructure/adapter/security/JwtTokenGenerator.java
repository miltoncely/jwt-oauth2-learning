package com.learning.authserver.infrastructure.adapter.security;

import com.learning.authserver.application.port.TokenGenerator;
import com.learning.authserver.domain.model.User;
import com.learning.shared.constant.SecurityConstants;
import com.learning.shared.security.JwtUtils;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenGenerator implements TokenGenerator {

    private final RSAKeyProvider keyProvider;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    @Override
    public Mono<String> generateAccessToken(User user, Map<String, Object> additionalClaims) {
        log.debug("Generando access token para usuario: {}", user.getUsername());

        return Mono.fromCallable(() -> {
            PrivateKey privateKey = keyProvider.getPrivateKey();
            Date now = new Date();
            Date expiration = new Date(now.getTime() + accessTokenValidity);

            Map<String, Object> claims = new HashMap<>();
            claims.put(SecurityConstants.CLAIM_EMAIL, user.getEmail());
            claims.put(SecurityConstants.CLAIM_NAME, user.getFullName());
            claims.put(SecurityConstants.CLAIM_ROLES, user.getRoleNames());
            claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);

            if (additionalClaims != null) {
                claims.putAll(additionalClaims);
            }

            String token = Jwts.builder()
                    .claims(claims)
                    .subject(user.getUsername())
                    .issuer(issuer)
                    .audience().add(audience).and()
                    .issuedAt(now)
                    .expiration(expiration)
                    .signWith(privateKey)
                    .compact();

            log.debug("Access token generado (expira en {}ms)", accessTokenValidity);
            return token;
        });
    }

    @Override
    public Mono<String> generateClientToken(String clientId, String scopes) {
        log.debug("Generando token para cliente: {}", clientId);

        return Mono.fromCallable(() -> {
            PrivateKey privateKey = keyProvider.getPrivateKey();
            Date now = new Date();
            Date expiration = new Date(now.getTime() + accessTokenValidity);

            Map<String, Object> claims = new HashMap<>();
            claims.put(SecurityConstants.CLAIM_SCOPE, scopes);
            claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);

            String token = Jwts.builder()
                    .claims(claims)
                    .subject(clientId)
                    .issuer(issuer)
                    .audience().add(audience).and()
                    .issuedAt(now)
                    .expiration(expiration)
                    .signWith(privateKey)
                    .compact();

            log.debug("Token de cliente generado");
            return token;
        });
    }

    @Override
    public Mono<String> generateRefreshToken(User user) {
        log.debug("Generando refresh token para usuario: {}", user.getUsername());

        return Mono.fromCallable(() -> {
            PrivateKey privateKey = keyProvider.getPrivateKey();
            Date now = new Date();
            Date expiration = new Date(now.getTime() + refreshTokenValidity);

            Map<String, Object> claims = new HashMap<>();
            claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_REFRESH);

            String token = Jwts.builder()
                    .claims(claims)
                    .subject(user.getUsername())
                    .issuer(issuer)
                    .audience().add(audience).and()
                    .issuedAt(now)
                    .expiration(expiration)
                    .signWith(privateKey)
                    .compact();

            log.debug("Refresh token generado (expira en {}ms)", refreshTokenValidity);
            return token;
        });
    }

    @Override
    public Mono<String> validateRefreshToken(String refreshToken) {
        log.debug("Validando refresh token");

        return Mono.fromCallable(() -> {
                    return JwtUtils.extractSubject(refreshToken, keyProvider.getPrivateKey());
                }).doOnSuccess(subject -> log.debug("Refresh token válido para: {}", subject))
                .doOnError(error -> log.error("Refresh token inválido: {}", error.getMessage()));
    }
}