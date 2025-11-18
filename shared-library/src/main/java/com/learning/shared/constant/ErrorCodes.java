package com.learning.shared.constant;

/**
 * Códigos de error estandarizados para toda la aplicación.
 * Permite identificar errores de forma única y consistente.
 */
public final class ErrorCodes {

    private ErrorCodes() {
        // Clase de utilidad, no debe instanciarse
    }

    // ERRORES DE AUTENTICACIÓN (1xxx)
    public static final String AUTH_001 = "AUTH-001"; // Credenciales inválidas
    public static final String AUTH_002 = "AUTH-002"; // Usuario no encontrado
    public static final String AUTH_003 = "AUTH-003"; // Cuenta deshabilitada
    public static final String AUTH_004 = "AUTH-004"; // Cuenta bloqueada
    public static final String AUTH_005 = "AUTH-005"; // Contraseña expirada

    // ERRORES DE TOKEN (2xxx)
    public static final String TOKEN_001 = "TOKEN-001"; // Token ausente
    public static final String TOKEN_002 = "TOKEN-002"; // Token inválido
    public static final String TOKEN_003 = "TOKEN-003"; // Token expirado
    public static final String TOKEN_004 = "TOKEN-004"; // Token revocado
    public static final String TOKEN_005 = "TOKEN-005"; // Firma inválida
    public static final String TOKEN_006 = "TOKEN-006"; // Formato incorrecto
    public static final String TOKEN_007 = "TOKEN-007"; // Claims faltantes

    // ERRORES DE AUTORIZACIÓN (3xxx)
    public static final String AUTHZ_001 = "AUTHZ-001"; // Acceso denegado
    public static final String AUTHZ_002 = "AUTHZ-002"; // Permisos insuficientes
    public static final String AUTHZ_003 = "AUTHZ-003"; // Rol requerido no presente
    public static final String AUTHZ_004 = "AUTHZ-004"; // Scope requerido no presente

    // ERRORES DE CLIENTE OAUTH (4xxx)
    public static final String CLIENT_001 = "CLIENT-001"; // Cliente no encontrado
    public static final String CLIENT_002 = "CLIENT-002"; // Cliente deshabilitado
    public static final String CLIENT_003 = "CLIENT-003"; // Secret inválido
    public static final String CLIENT_004 = "CLIENT-004"; // Grant type no soportado
    public static final String CLIENT_005 = "CLIENT-005"; // Redirect URI inválido

    // ERRORES DE VALIDACIÓN (5xxx)
    public static final String VALIDATION_001 = "VALIDATION-001"; // Datos inválidos
    public static final String VALIDATION_002 = "VALIDATION-002"; // Campo requerido
    public static final String VALIDATION_003 = "VALIDATION-003"; // Formato inválido
    public static final String VALIDATION_004 = "VALIDATION-004"; // Valor fuera de rango

    // ERRORES DE SISTEMA (9xxx)
    public static final String SYSTEM_001 = "SYSTEM-001"; // Error interno del servidor
    public static final String SYSTEM_002 = "SYSTEM-002"; // Servicio no disponible
    public static final String SYSTEM_003 = "SYSTEM-003"; // Timeout
    public static final String SYSTEM_004 = "SYSTEM-004"; // Error de configuración
}