package com.learning.shared.constant;

/**
 * Constantes relacionadas con seguridad y JWT.
 * Centraliza valores que se usan en múltiples módulos.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Clase de utilidad, no debe instanciarse
    }

    // HEADERS HTTP
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";

    // JWT CLAIMS
    public static final String CLAIM_SUBJECT = "sub";
    public static final String CLAIM_ISSUED_AT = "iat";
    public static final String CLAIM_EXPIRATION = "exp";
    public static final String CLAIM_ISSUER = "iss";
    public static final String CLAIM_AUDIENCE = "aud";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_SCOPE = "scope";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_NAME = "name";
    public static final String CLAIM_TOKEN_TYPE = "token_type";

    // VALORES POR DEFECTO
    public static final String DEFAULT_ISSUER = "auth-server";
    public static final String DEFAULT_AUDIENCE = "resource-server";
    public static final long ACCESS_TOKEN_VALIDITY = 3600000L; // 1 hora
    public static final long REFRESH_TOKEN_VALIDITY = 604800000L; // 7 días
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    // ALGORITMOS
    public static final String ALGORITHM_RS256 = "RS256";
    public static final String ALGORITHM_HS256 = "HS256";

    // ROLES
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String AUTHORITY_PREFIX = "ROLE_";

    // SCOPES OAUTH 2.0
    public static final String SCOPE_READ = "read";
    public static final String SCOPE_WRITE = "write";
    public static final String SCOPE_DELETE = "delete";
    public static final String SCOPE_SEPARATOR = " ";
}