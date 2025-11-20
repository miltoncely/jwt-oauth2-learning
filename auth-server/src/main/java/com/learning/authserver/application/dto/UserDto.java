package com.learning.authserver.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.authserver.domain.model.User;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
    private Boolean enabled;
    private LocalDateTime createdAt;

    public static UserDto fromDomain(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoleNames())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}