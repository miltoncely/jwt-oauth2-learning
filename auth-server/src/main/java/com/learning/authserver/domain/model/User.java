package com.learning.authserver.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String username;

    private String email;

    private String password;

    private String fullName;

    private List<Role> roles;

    private Boolean enabled;

    private Boolean locked;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public boolean hasRole(String roleName) {
        if (roles == null) {
            return false;
        }
        return roles.stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    public boolean canAuthenticate() {
        return Boolean.TRUE.equals(enabled) && !Boolean.TRUE.equals(locked);
    }

    public List<String> getRoleNames() {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::getName)
                .toList();
    }
}