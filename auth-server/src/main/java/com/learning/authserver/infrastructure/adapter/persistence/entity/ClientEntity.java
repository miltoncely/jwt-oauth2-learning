package com.learning.authserver.infrastructure.adapter.persistence.entity;

import com.learning.authserver.domain.model.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("oauth_clients")
public class ClientEntity {

    @Id
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

    public Client toDomain() {
        return Client.builder()
                .id(this.id)
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .clientName(this.clientName)
                .allowedScopes(this.allowedScopes)
                .grantTypes(this.grantTypes)
                .redirectUris(this.redirectUris)
                .enabled(this.enabled)
                .accessTokenValidity(this.accessTokenValidity)
                .refreshTokenValidity(this.refreshTokenValidity)
                .build();
    }

    public static ClientEntity fromDomain(Client client) {
        return ClientEntity.builder()
                .id(client.getId())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientName(client.getClientName())
                .allowedScopes(client.getAllowedScopes())
                .grantTypes(client.getGrantTypes())
                .redirectUris(client.getRedirectUris())
                .enabled(client.getEnabled())
                .accessTokenValidity(client.getAccessTokenValidity())
                .refreshTokenValidity(client.getRefreshTokenValidity())
                .build();
    }
}