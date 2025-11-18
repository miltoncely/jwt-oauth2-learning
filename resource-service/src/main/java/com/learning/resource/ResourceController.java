package com.learning.resource;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class ResourceController {

    @GetMapping("/public/info")
    public Mono<Map<String, String>> publicInfo() {
        return Mono.just(Map.of("message", "This is public information", "status", "accessible"));
    }

    @GetMapping("/api/secure-data")
    public Mono<Map<String, Object>> secureData(@AuthenticationPrincipal Jwt jwt) {
        return Mono.just(Map.of(
                "message", "This is SECURE data",
                "user", jwt.getSubject(),
                "claims", jwt.getClaims()
        ));
    }
}
