package com.learning.authserver.domain.service;

import com.learning.authserver.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de dominio para validaciones relacionadas con tokens.
 *
 * Los servicios de dominio encapsulan lógica de negocio que no pertenece
 * naturalmente a una entidad específica o que involucra múltiples entidades.
 */
@Slf4j
@Service
public class TokenValidationService {

    /**
     * Valida si un usuario puede recibir un token de acceso.
     * Verifica reglas de negocio como estado de la cuenta, roles, etc.
     *
     * @param user Usuario a validar
     * @return true si puede recibir token, false en caso contrario
     */
    public boolean canUserReceiveToken(User user) {
        log.debug("Validando si usuario puede recibir token: {}", user.getUsername());

        // Regla 1: Usuario debe estar habilitado
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("Usuario deshabilitado no puede recibir token: {}", user.getUsername());
            return false;
        }

        // Regla 2: Usuario no debe estar bloqueado
        if (Boolean.TRUE.equals(user.getLocked())) {
            log.warn("Usuario bloqueado no puede recibir token: {}", user.getUsername());
            return false;
        }

        // Regla 3: Usuario debe tener al menos un rol
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            log.warn("Usuario sin roles no puede recibir token: {}", user.getUsername());
            return false;
        }

        log.debug("Usuario puede recibir token: {}", user.getUsername());
        return true;
    }

    /**
     * Valida si los scopes solicitados son válidos para un usuario.
     * Verifica que el usuario tenga los roles necesarios para los scopes.
     *
     * @param user Usuario
     * @param requestedScopes Scopes solicitados (ej: "read write")
     * @return true si los scopes son válidos
     */
    public boolean validateScopesForUser(User user, String requestedScopes) {
        if (requestedScopes == null || requestedScopes.isBlank()) {
            return true; // Sin scopes solicitados es válido
        }

        log.debug("Validando scopes '{}' para usuario: {}", requestedScopes, user.getUsername());

        // Separar los scopes
        String[] scopes = requestedScopes.split(" ");

        for (String scope : scopes) {
            if (!canUserAccessScope(user, scope)) {
                log.warn("Usuario {} no tiene permiso para scope: {}", user.getUsername(), scope);
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica si un usuario puede acceder a un scope específico.
     * Implementa las reglas de negocio de permisos por scope.
     */
    private boolean canUserAccessScope(User user, String scope) {
        return switch (scope.toLowerCase()) {
            case "read" -> true; // Todos pueden leer

            case "write" -> user.hasRole("ROLE_USER") || user.hasRole("ROLE_ADMIN");

            case "delete" -> user.hasRole("ROLE_ADMIN");

            case "admin" -> user.hasRole("ROLE_ADMIN");

            default -> {
                log.warn("Scope desconocido: {}", scope);
                yield false;
            }
        };
    }

    /**
     * Determina la duración apropiada del token según el usuario y contexto.
     *
     * @param user Usuario
     * @param defaultValidity Duración por defecto en segundos
     * @return Duración ajustada del token
     */
    public long determineTokenValidity(User user, long defaultValidity) {
        // Los administradores pueden tener tokens de mayor duración
        if (user.hasRole("ROLE_ADMIN")) {
            return defaultValidity * 2; // El doble de duración para admins
        }

        return defaultValidity;
    }

    /**
     * Valida si se debe permitir el refresh de un token.
     * Verifica reglas de negocio adicionales para refresh tokens.
     *
     * @param user Usuario
     * @param lastRefreshTime Última vez que se refrescó (puede ser null)
     * @return true si se permite refresh
     */
    public boolean canRefreshToken(User user, LocalDateTime lastRefreshTime) {
        // El usuario debe poder autenticarse
        if (!user.canAuthenticate()) {
            return false;
        }

        // Si hay un tiempo de último refresh, verificar que no sea muy reciente
        // (protección contra abuso de refresh)
        if (lastRefreshTime != null) {
            LocalDateTime now = LocalDateTime.now();
            long minutesSinceLastRefresh = java.time.Duration.between(lastRefreshTime, now).toMinutes();

            // No permitir refresh si fue hace menos de 1 minuto
            if (minutesSinceLastRefresh < 1) {
                log.warn("Refresh token solicitado muy pronto para usuario: {}", user.getUsername());
                return false;
            }
        }

        return true;
    }

    /**
     * Enriquece los claims de un token según el usuario.
     * Agrega información adicional basada en reglas de negocio.
     *
     * @param user Usuario
     * @return Mapa con claims adicionales
     */
    public java.util.Map<String, Object> enrichTokenClaims(User user) {
        java.util.Map<String, Object> additionalClaims = new java.util.HashMap<>();

        // Agregar metadata del usuario
        additionalClaims.put("email", user.getEmail());
        additionalClaims.put("name", user.getFullName());
        additionalClaims.put("roles", user.getRoleNames());

        // Agregar información de permisos
        List<String> permissions = determinePermissions(user);
        additionalClaims.put("permissions", permissions);

        // Agregar flag de admin
        additionalClaims.put("is_admin", user.hasRole("ROLE_ADMIN"));

        return additionalClaims;
    }

    /**
     * Determina los permisos específicos del usuario.
     */
    private List<String> determinePermissions(User user) {
        List<String> permissions = new java.util.ArrayList<>();

        if (user.hasRole("ROLE_USER")) {
            permissions.add("read:own");
            permissions.add("write:own");
        }

        if (user.hasRole("ROLE_ADMIN")) {
            permissions.add("read:all");
            permissions.add("write:all");
            permissions.add("delete:all");
            permissions.add("admin:users");
        }

        return permissions;
    }
}