package com.learning.auth.api;

import com.learning.auth.security.JwtService;
import com.learning.auth.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/oauth/token")
    public Mono<ResponseEntity<Map<String, String>>> token(
            @RequestParam("grant_type") String grantType,
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        if (!"password".equals(grantType)) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return userRepository.findByUsername(username)
                .filter(user -> {
                    // Simple password check (handling {noop} prefix if present)
                    String dbPass = user.getPassword().replace("{noop}", "");
                    return dbPass.equals(password);
                })
                .map(user -> {
                    String token = jwtService.generateToken(user.getUsername(), user.getRoles());
                    return ResponseEntity.ok(Map.of(
                            "access_token", token,
                            "token_type", "Bearer",
                            "expires_in", "3600"
                    ));
                })
                .defaultIfEmpty(ResponseEntity.status(401).build());

    }

    @PostMapping("/oauth/revoke")
    public Mono<ResponseEntity<Void>> revoke(@RequestParam("token") String token) {
        return jwtService.revokeToken(token)
                .map(deleted -> ResponseEntity.ok().<Void>build());
    }
}
