package com.learning.resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/api/admin")
    public Mono<Map<String, String>> adminData() {
        return Mono.just(Map.of("message", "Hello Admin!", "role", "ADMIN"));
    }

    @GetMapping("/api/user")
    public Mono<Map<String, String>> userData() {
        return Mono.just(Map.of("message", "Hello User!", "role", "USER"));
    }

    @GetMapping("/api/users/{username}")
    @PreAuthorize("#username == authentication.name")
    public Mono<Map<String, String>> getData(@PathVariable String username) {
        return Mono.just(Map.of("message", "Esto no lo puede ver el admin", "role", "USER"));
    }
}
