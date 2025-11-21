package com.learning.authserver.application.usecase;

import com.learning.authserver.application.port.PasswordEncoder;
import com.learning.authserver.domain.model.User;
import com.learning.authserver.domain.repository.UserRepository;
import com.learning.shared.constant.ErrorCodes;
import com.learning.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Autenticar un usuario.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> execute(String username, String password) {
        log.debug("Intentando autenticar usuario: {}", username);

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        401
                )))
                .flatMap(user -> validateUserStatus(user))
                .flatMap(user -> validatePassword(user, password))
                .doOnSuccess(user -> log.info("Usuario autenticado exitosamente: {}", username))
                .doOnError(error -> log.warn("Autenticación fallida para {}: {}", username, error.getMessage()));
    }

    private Mono<User> validateUserStatus(User user) {
        if (Boolean.TRUE.equals(user.getLocked())) {
            log.warn("Intento de acceso a cuenta bloqueada: {}", user.getUsername());
            return Mono.error(new BusinessException(
                    "La cuenta está bloqueada. Contacte al administrador.",
                    ErrorCodes.AUTH_004,
                    403
            ));
        }

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("Intento de acceso a cuenta deshabilitada: {}", user.getUsername());
            return Mono.error(new BusinessException(
                    "La cuenta está deshabilitada. Contacte al administrador.",
                    ErrorCodes.AUTH_003,
                    403
            ));
        }

        return Mono.just(user);
    }

    private Mono<User> validatePassword(User user, String plainPassword) {
        if (!passwordEncoder.matches(plainPassword, user.getPassword())) {
            log.warn("Contraseña incorrecta para usuario: {}", user.getUsername());
            return Mono.error(new BusinessException(
                    "Credenciales inválidas",
                    ErrorCodes.AUTH_001,
                    401
            ));
        }

        return Mono.just(user);
    }

    public Mono<User> executeByEmail(String email, String password) {
        log.debug("Intentando autenticar usuario por email: {}", email);

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        401
                )))
                .flatMap(user -> validateUserStatus(user))
                .flatMap(user -> validatePassword(user, password))
                .doOnSuccess(user -> log.info("Usuario autenticado exitosamente por email: {}", email));
    }

    public Mono<Boolean> userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}