package com.learning.authserver.presentation.controller;

import com.learning.authserver.application.dto.UserDto;
import com.learning.authserver.application.port.PasswordEncoder;
import com.learning.authserver.domain.model.Role;
import com.learning.authserver.domain.model.User;
import com.learning.authserver.domain.repository.UserRepository;
import com.learning.shared.constant.ErrorCodes;
import com.learning.shared.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller para administración de usuarios.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Flux<UserDto> getAllUsers() {
        log.info("Obteniendo lista de usuarios");

        return userRepository.findAll()
                .map(UserDto::fromDomain)
                .doOnComplete(() -> log.debug("Lista de usuarios retornada"));
    }

    @GetMapping("/{id}")
    public Mono<UserDto> getUserById(@PathVariable Long id) {
        log.info("Buscando usuario por ID: {}", id);

        return userRepository.findById(id)
                .map(UserDto::fromDomain)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        404
                )));
    }

    @GetMapping("/by-username/{username}")
    public Mono<UserDto> getUserByUsername(@PathVariable String username) {
        log.info("Buscando usuario por username: {}", username);

        return userRepository.findByUsername(username)
                .map(UserDto::fromDomain)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        404
                )));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creando nuevo usuario: {}", request.getUsername());

        return validateUserDoesNotExist(request)
                .then(buildUserFromRequest(request))
                .flatMap(userRepository::save)
                .map(UserDto::fromDomain)
                .doOnSuccess(user -> log.info("Usuario creado: {}", user.getUsername()));
    }

    @PutMapping("/{id}")
    public Mono<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("Actualizando usuario ID: {}", id);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        404
                )))
                .flatMap(existingUser -> applyUpdates(existingUser, request))
                .flatMap(userRepository::save)
                .map(UserDto::fromDomain)
                .doOnSuccess(user -> log.info("Usuario actualizado: {}", user.getUsername()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        log.info("Eliminando usuario ID: {}", id);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        404
                )))
                .flatMap(user -> userRepository.deleteById(id))
                .doOnSuccess(v -> log.info("Usuario eliminado ID: {}", id));
    }

    @PatchMapping("/{id}/status")
    public Mono<UserDto> toggleUserStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request
    ) {
        log.info("Cambiando estado de usuario ID {}: enabled={}", id, request.getEnabled());

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        404
                )))
                .flatMap(user -> {
                    user.setEnabled(request.getEnabled());
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .map(UserDto::fromDomain);
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request
    ) {
        log.info("Cambiando contraseña para usuario ID: {}", id);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "Usuario no encontrado",
                        ErrorCodes.AUTH_002,
                        404
                )))
                .flatMap(user -> {
                    String hashedPassword = passwordEncoder.encode(request.getNewPassword());
                    user.setPassword(hashedPassword);
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .then();
    }

    // ========== Métodos Auxiliares ==========

    private Mono<Void> validateUserDoesNotExist(CreateUserRequest request) {
        return userRepository.existsByUsername(request.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessException(
                                "El username ya está en uso",
                                ErrorCodes.VALIDATION_001,
                                400
                        ));
                    }
                    return Mono.empty();
                })
                .then(userRepository.existsByEmail(request.getEmail()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessException(
                                "El email ya está en uso",
                                ErrorCodes.VALIDATION_001,
                                400
                        ));
                    }
                    return Mono.empty();
                });
    }

    private Mono<User> buildUserFromRequest(CreateUserRequest request) {
        return Mono.fromCallable(() -> {
            String hashedPassword = passwordEncoder.encode(request.getPassword());

            List<Role> roles = request.getRoles().stream()
                    .map(roleName -> Role.builder().name(roleName).build())
                    .toList();

            return User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(hashedPassword)
                    .fullName(request.getFullName())
                    .roles(roles)
                    .enabled(true)
                    .locked(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        });
    }

    private Mono<User> applyUpdates(User existingUser, UpdateUserRequest request) {
        return Mono.fromCallable(() -> {
            if (request.getEmail() != null) {
                existingUser.setEmail(request.getEmail());
            }
            if (request.getFullName() != null) {
                existingUser.setFullName(request.getFullName());
            }
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                List<Role> roles = request.getRoles().stream()
                        .map(roleName -> Role.builder().name(roleName).build())
                        .toList();
                existingUser.setRoles(roles);
            }

            existingUser.setUpdatedAt(LocalDateTime.now());
            return existingUser;
        });
    }

    // ========== DTOs de Request ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        @jakarta.validation.constraints.NotBlank
        private String username;

        @jakarta.validation.constraints.Email
        @jakarta.validation.constraints.NotBlank
        private String email;

        @jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Size(min = 6)
        private String password;

        private String fullName;

        @jakarta.validation.constraints.NotEmpty
        private List<String> roles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        @jakarta.validation.constraints.Email
        private String email;

        private String fullName;
        private List<String> roles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusRequest {
        private Boolean enabled;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        @jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Size(min = 6)
        private String newPassword;
    }
}