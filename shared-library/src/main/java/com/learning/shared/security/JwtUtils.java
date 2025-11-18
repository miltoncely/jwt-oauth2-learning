package com.learning.shared.security;

import com.learning.shared.constant.SecurityConstants;
import com.learning.shared.dto.TokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public final class JwtUtils {

    private JwtUtils() {
        // Clase de utilidad, no debe instanciarse
    }

    public static Claims extractAllClaims(String token, Key publicKey) {
        return Jwts.parser()
                .verifyWith((java.security.PublicKey) publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static <T> T extractClaim(String token, Key publicKey, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, publicKey);
        return claimsResolver.apply(claims);
    }

    public static String extractSubject(String token, Key publicKey) {
        return extractClaim(token, publicKey, Claims::getSubject);
    }

    public static Date extractExpiration(String token, Key publicKey) {
        return extractClaim(token, publicKey, Claims::getExpiration);
    }

    @SuppressWarnings("unchecked")
    public static List<String> extractRoles(String token, Key publicKey) {
        Claims claims = extractAllClaims(token, publicKey);
        return claims.get(SecurityConstants.CLAIM_ROLES, List.class);
    }

    public static String extractScope(String token, Key publicKey) {
        Claims claims = extractAllClaims(token, publicKey);
        return claims.get(SecurityConstants.CLAIM_SCOPE, String.class);
    }

    public static boolean isTokenExpired(String token, Key publicKey) {
        try {
            Date expiration = extractExpiration(token, publicKey);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public static boolean validateToken(String token, Key publicKey) {
        try {
            extractAllClaims(token, publicKey);
            return !isTokenExpired(token, publicKey);
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.error("Firma invalida: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Token mal formado: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }

    public static String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length());
    }

    @SuppressWarnings("unchecked")
    public static TokenClaims parseTokenClaims(String token, Key publicKey) {
        Claims claims = extractAllClaims(token, publicKey);

        return TokenClaims.builder()
                .subject(claims.getSubject())
                .email(claims.get(SecurityConstants.CLAIM_EMAIL, String.class))
                .name(claims.get(SecurityConstants.CLAIM_NAME, String.class))
                .roles(claims.get(SecurityConstants.CLAIM_ROLES, List.class))
                .scope(claims.get(SecurityConstants.CLAIM_SCOPE, String.class))
                .issuer(claims.getIssuer())
                .audience(claims.getAudience() != null && !claims.getAudience().isEmpty() ?
                        claims.getAudience().iterator().next() : null)
                .issuedAt(claims.getIssuedAt() != null ? claims.getIssuedAt().getTime() / 1000 : null)
                .expiration(claims.getExpiration() != null ? claims.getExpiration().getTime() / 1000 : null)
                .tokenType(claims.get(SecurityConstants.CLAIM_TOKEN_TYPE, String.class))
                .build();
    }

    public static String generateToken(
            Map<String, Object> claims,
            String subject,
            Key privateKey,
            long validityMillis
    ) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityMillis);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey)
                .compact();
    }

    public static void printTokenInfo(String token, Key publicKey) {
        try {
            TokenClaims claims = parseTokenClaims(token, publicKey);
            log.info("Token Information:");
            log.info("  Subject: {}", claims.getSubject());
            log.info("  Email: {}", claims.getEmail());
            log.info("  Roles: {}", claims.getRoles());
            log.info("  Scope: {}", claims.getScope());
            log.info("  Issuer: {}", claims.getIssuer());
            log.info("  Audience: {}", claims.getAudience());
            log.info("  Issued At: {}", claims.getIssuedAt() != null ? new Date(claims.getIssuedAt() * 1000) : null);
            log.info("  Expires At: {}", claims.getExpiration() != null ? new Date(claims.getExpiration() * 1000) : null);
            log.info("  Expired: {}", claims.isExpired());
        } catch (Exception e) {
            log.error("Error printing token info: {}", e.getMessage());
        }
    }
}