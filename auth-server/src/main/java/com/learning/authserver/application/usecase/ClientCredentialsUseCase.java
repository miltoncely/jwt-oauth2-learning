package com.learning.authserver.application.usecase;

import com.learning.authserver.application.dto.TokenResponse;
import com.learning.authserver.application.port.TokenGenerator;
import com.learning.authserver.domain.model.Client;
import com.learning.authserver.domain.repository.ClientRepository;
import com.learning.shared.constant.ErrorCodes;
import com.learning.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCredentialsUseCase {

    private final ClientRepository clientRepository;
    private final TokenGenerator tokenGenerator;

    public Mono<TokenResponse> execute(String clientId, String clientSecret, String scope) {
        log.debug("Procesando client_credentials para: {}", clientId);

        return clientRepository.findByClientId(clientId)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Cliente no encontrado",
                        ErrorCodes.AUTH_002,
                        401
                )))
                .flatMap(client -> validateClient(client, clientSecret))
                .flatMap(client -> generateToken(client, scope))
                .doOnSuccess(r -> log.info("Token generado para cliente: {}", clientId));
    }

    private Mono<Client> validateClient(Client client, String clientSecret) {
        if (!Boolean.TRUE.equals(client.getEnabled())) {
            return Mono.error(new BusinessException(
                    "Cliente deshabilitado",
                    ErrorCodes.AUTH_003,
                    401
            ));
        }

        if (!client.getClientSecret().equals(clientSecret)) {
            return Mono.error(new BusinessException(
                    "Credenciales de cliente inválidas",
                    ErrorCodes.AUTH_001,
                    401
            ));
        }

        return Mono.just(client);
    }

    private Mono<TokenResponse> generateToken(Client client, String scope) {
        // Validar scopes si es necesario (lógica simplificada)
        String finalScope = (scope == null || scope.isBlank()) ? client.getAllowedScopes() : scope;

        return tokenGenerator.generateClientToken(client.getClientId(), finalScope)
                .map(token -> TokenResponse.builder()
                        .accessToken(token)
                        .tokenType("Bearer")
                        .expiresIn(client.getAccessTokenValidity().longValue() * 1000)
                        .scope(finalScope)
                        .issuedAt(System.currentTimeMillis() / 1000)
                        .build());
    }
}
