package com.learning.resourceserver.infrastructure.adapter.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.learning.resourceserver.application.port.TokenValidator;
import com.learning.resourceserver.domain.model.UserPrincipal;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator implements TokenValidator {

    private final RSAKeyLoader rsaKeyLoader;
    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        RSAPublicKey publicKey = rsaKeyLoader.loadPublicKey();
        this.algorithm = Algorithm.RSA256(publicKey, null);
    }

    @Override
    public Mono<UserPrincipal> validate(String token) {
        return Mono.fromCallable(() -> {
            DecodedJWT jwt = JWT.require(algorithm)
                    .build()
                    .verify(token);

            return UserPrincipal.builder()
                    .id(jwt.getSubject())
                    .username(jwt.getClaim("username").asString())
                    .roles(jwt.getClaim("roles").asList(String.class))
                    .build();
        });
    }
}
