package com.learning.authserver.application.usecase;

import com.learning.authserver.application.dto.TokenResponse;
import com.learning.authserver.application.port.TokenGenerator;
import com.learning.authserver.domain.model.User;
import com.learning.authserver.domain.repository.UserRepository;
import com.learning.authserver.domain.service.TokenValidationService;
import com.learning.shared.constant.ErrorCodes;
import com.learning.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    private final TokenValidationService tokenValidationService;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    public Mono<TokenResponse> execute(String refreshToken) {
        log.debug("Procesando refresh token");

        return tokenGenerator.validateRefreshToken(refreshToken)
                .flatMap(username -> userRepository.findByUsername(username))
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado para el token",
                        ErrorCodes.AUTH_002,
                        401
                )))
                .flatMap(this::validateUserAndGenerateTokens);
    }

    private Mono<TokenResponse> validateUserAndGenerateTokens(User user) {
        if (!tokenValidationService.canUserReceiveToken(user)) {
            return Mono.error(new BusinessException(
                    "Usuario no autorizado para renovar token",
                    ErrorCodes.AUTH_003,
                    403
            ));
        }

        Map<String, Object> claims = tokenValidationService.enrichTokenClaims(user);
        
        Mono<String> accessTokenMono = tokenGenerator.generateAccessToken(user, claims);
        Mono<String> refreshTokenMono = tokenGenerator.generateRefreshToken(user);

        return Mono.zip(accessTokenMono, refreshTokenMono)
                .map(tuple -> TokenResponse.builder()
                        .accessToken(tuple.getT1())
                        .refreshToken(tuple.getT2())
                        .tokenType("Bearer")
                        .expiresIn(refreshTokenValidity)
                        .issuedAt(System.currentTimeMillis() / 1000)
                        .build());
    }
}
