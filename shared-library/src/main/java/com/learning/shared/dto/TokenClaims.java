package com.learning.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenClaims {

    private String subject;
    private String email;
    private String name;
    private List<String> roles;
    private String scope;
    private String issuer;
    private String audience;
    private Long issuedAt;
    private Long expiration;
    private String tokenType;

    public boolean isExpired() {
        if (expiration == null) {
            return false;
        }
        return expiration < (System.currentTimeMillis() / 1000);
    }

    public boolean hasRole(String role) {
        if (roles == null) {
            return false;
        }
        return roles.contains(role);
    }

    public boolean hasScope(String requestedScope) {
        if (scope == null) {
            return false;
        }
        return scope.contains(requestedScope);
    }
}
