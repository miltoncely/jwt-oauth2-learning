package com.learning.authserver.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scope {
    private String name;
    private String description;

    public static String toString(List<Scope> scopes) {
        if (scopes == null || scopes.isEmpty()) return "";
        return scopes.stream()
                .map(Scope::getName)
                .collect(Collectors.joining(" "));
    }

    public static List<Scope> fromString(String scopesString) {
        if (scopesString == null || scopesString.isBlank()) return List.of();
        return Arrays.stream(scopesString.split(" "))
                .map(name -> Scope.builder().name(name).build())
                .collect(Collectors.toList());
    }
}
