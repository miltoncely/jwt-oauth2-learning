package com.learning.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/oauth/token")
    public Mono<ResponseEntity<Map<String, String>>> token(
            @RequestParam("grant_type") String grantType,
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        // Simplified logic: In a real app, validate username/password against DB
        if ("password".equals(grantType) && "admin".equals(username) && "123456".equals(password)) {
            String token = jwtService.generateToken(username);
            return Mono.just(ResponseEntity.ok(Map.of(
                    "access_token", token,
                    "token_type", "Bearer",
                    "expires_in", "3600"
            )));
        }

        return Mono.just(ResponseEntity.status(401).build());
    }
}
