package com.learning.authserver.infrastructure.adapter.persistence.entity;

import com.learning.authserver.domain.model.Role;
import com.learning.authserver.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserEntity {

    @Id
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String roles;
    private Boolean enabled;
    private Boolean locked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User toDomain() {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .password(this.password)
                .fullName(this.fullName)
                .roles(parseRoles(this.roles))
                .enabled(this.enabled)
                .locked(this.locked)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public static UserEntity fromDomain(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .roles(rolesToString(user.getRoles()))
                .enabled(user.getEnabled())
                .locked(user.getLocked())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private List<Role> parseRoles(String rolesStr) {
        if (rolesStr == null || rolesStr.isBlank()) {
            return List.of();
        }
        return Arrays.stream(rolesStr.split(","))
                .map(String::trim)
                .map(name -> Role.builder()
                        .name(name)
                        .build())
                .toList();
    }

    private static String rolesToString(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return "";
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));
    }
}