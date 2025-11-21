package com.learning.authserver.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long id;
    private String clientId;
    private String clientSecret;
    private String clientName;
    private String allowedScopes;
    private String grantTypes;
    private String redirectUris;
    private Boolean enabled;
    private Integer accessTokenValidity;
    private Integer refreshTokenValidity;

    public boolean hasGrantType(String grantType) {
        if (grantTypes == null) return false;
        return grantTypes.contains(grantType);
    }

    public boolean hasScope(String scope) {
        if (allowedScopes == null) return false;
        return allowedScopes.contains(scope);
    }
}
