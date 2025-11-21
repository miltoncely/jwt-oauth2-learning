package com.learning.authserver.application.usecase;

import com.learning.authserver.application.dto.TokenResponse;
import com.learning.authserver.application.port.PasswordEncoder;
import com.learning.authserver.application.port.TokenGenerator;
import com.learning.authserver.domain.model.User;
import com.learning.authserver.domain.repository.UserRepository;
import com.learning.shared.constant.ErrorCodes;
import com.learning.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateTokenUseCase {

    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    public Mono<TokenResponse> execute(String username, String password, String scope) {
        log.info("Iniciando autenticación para usuario: {}", username);

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        401
                )))
                .flatMap(user -> validateUser(user, password))
                .flatMap(user -> generateTokens(user, scope))
                .doOnSuccess(response -> log.info("Token generado exitosamente para: {}", username))
                .doOnError(error -> log.error("Error generando token para {}: {}", username, error.getMessage()));
    }

    private Mono<User> validateUser(User user, String password) {
        if (!user.canAuthenticate()) {
            if (Boolean.TRUE.equals(user.getLocked())) {
                return Mono.error(new BusinessException(
                        "Cuenta bloqueada",
                        ErrorCodes.AUTH_004,
                        401
                ));
            }
            return Mono.error(new BusinessException(
                    "Cuenta deshabilitada",
                    ErrorCodes.AUTH_003,
                    401
            ));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Mono.error(new BusinessException(
                    "Credenciales inválidas",
                    ErrorCodes.AUTH_001,
                    401
            ));
        }

        return Mono.just(user);
    }

    private Mono<TokenResponse> generateTokens(User user, String scope) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("name", user.getFullName());
        claims.put("roles", user.getRoleNames());

        if (scope != null && !scope.isBlank()) {
            claims.put("scope", scope);
        }

        Mono<String> accessTokenMono = tokenGenerator.generateAccessToken(user, claims);

        Mono<String> refreshTokenMono = tokenGenerator.generateRefreshToken(user);

        return Mono.zip(accessTokenMono, refreshTokenMono)
                .map(tokens -> TokenResponse.builder()
                        .accessToken(tokens.getT1())
                        .refreshToken(tokens.getT2())
                        .tokenType("Bearer")
                        .expiresIn(refreshTokenValidity)
                        .scope(scope)
                        .issuedAt(System.currentTimeMillis() / 1000)
                        .build());
    }
}
