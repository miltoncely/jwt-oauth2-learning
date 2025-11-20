package com.learning.authserver.presentation.controller;

import com.learning.authserver.application.dto.LoginRequest;
import com.learning.authserver.application.dto.TokenRequest;
import com.learning.authserver.application.dto.TokenResponse;
import com.learning.authserver.application.usecase.ClientCredentialsUseCase;
import com.learning.authserver.application.usecase.GenerateTokenUseCase;
import com.learning.authserver.application.usecase.RefreshTokenUseCase;
import com.learning.shared.constant.ErrorCodes;
import com.learning.shared.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controller REST para endpoints OAuth 2.0.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final GenerateTokenUseCase generateTokenUseCase;
    private final ClientCredentialsUseCase clientCredentialsUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping(
            value = "/oauth/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<TokenResponse> token(@Valid @ModelAttribute TokenRequest request) {
        log.info("Solicitud de token recibida: grant_type={}", request.getGrantType());

        return switch (request.getGrantType().toLowerCase()) {
            case "client_credentials" -> handleClientCredentials(request);
            case "password" -> handlePasswordGrant(request);
            case "refresh_token" -> handleRefreshToken(request);
            default -> Mono.error(new BusinessException(
                    "Grant type no soportado: " + request.getGrantType(),
                    ErrorCodes.CLIENT_004,
                    400
            ));
        };
    }

    @PostMapping(
            value = "/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request para usuario: {}", request.getUsername());

        return generateTokenUseCase.execute(
                request.getUsername(),
                request.getPassword(),
                request.getScope()
        );
    }

    private Mono<TokenResponse> handleClientCredentials(TokenRequest request) {
        if (request.getClientId() == null || request.getClientSecret() == null) {
            return Mono.error(new BusinessException(
                    "client_id y client_secret son requeridos",
                    ErrorCodes.VALIDATION_002,
                    400
            ));
        }

        return clientCredentialsUseCase.execute(
                request.getClientId(),
                request.getClientSecret(),
                request.getScope()
        );
    }

    private Mono<TokenResponse> handlePasswordGrant(TokenRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Mono.error(new BusinessException(
                    "username y password son requeridos",
                    ErrorCodes.VALIDATION_002,
                    400
            ));
        }

        return generateTokenUseCase.execute(
                request.getUsername(),
                request.getPassword(),
                request.getScope()
        );
    }

    private Mono<TokenResponse> handleRefreshToken(TokenRequest request) {
        if (request.getRefreshToken() == null) {
            return Mono.error(new BusinessException(
                    "refresh_token es requerido",
                    ErrorCodes.VALIDATION_002,
                    400
            ));
        }

        return refreshTokenUseCase.execute(request.getRefreshToken());
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Auth Server is running");
    }
}