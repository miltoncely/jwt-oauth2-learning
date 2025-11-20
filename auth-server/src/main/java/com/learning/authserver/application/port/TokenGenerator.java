package com.learning.authserver.application.port;

import com.learning.authserver.domain.model.User;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TokenGenerator {
    Mono<String> generateAccessToken(User user, Map<String, Object> additionalClaims);
    Mono<String> generateClientToken(String clientId, String scopes);
    Mono<String> generateRefreshToken(User user);
    Mono<String> validateRefreshToken(String refreshToken);
}
