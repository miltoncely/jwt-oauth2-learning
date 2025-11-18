package com.learning.resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RedisTokenValidator implements OAuth2TokenValidator<Jwt> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public RedisTokenValidator(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        // This method is synchronous, but we need to check Redis asynchronously.
        // Spring Security 5+ Reactive JWT Decoder supports async validation via delegating to a reactive flow,
        // but the standard OAuth2TokenValidator interface is synchronous.
        //
        // However, NimbusReactiveJwtDecoder allows setting a custom validator.
        // A common pattern in reactive flows is to do this check in a filter or
        // use a blocking call (not recommended) or use a custom ReactiveJwtDecoder.
        //
        // For simplicity in this learning project, we will use a blocking call with a short timeout,
        // OR better, we can implement this check in the SecurityConfig by wrapping the decoder.
        //
        // Let's try a cleaner approach: We won't use OAuth2TokenValidator directly here because of the sync limitation.
        // Instead, we'll use a custom ReactiveJwtDecoder wrapper in SecurityConfig.
        return OAuth2TokenValidatorResult.success();
    }

    public Mono<Boolean> isValid(String token) {
        return redisTemplate.hasKey(token);
    }
}
