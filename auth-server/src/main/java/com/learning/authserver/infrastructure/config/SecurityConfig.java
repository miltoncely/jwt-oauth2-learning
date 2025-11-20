package com.learning.authserver.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad para el Authorization Server.
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("Configurando seguridad del Authorization Server");

        return http
                // Deshabilitar CSRF (típico en APIs REST stateless)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Configurar autorización de endpoints
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints públicos
                        .pathMatchers("/oauth/token").permitAll()
                        .pathMatchers("/auth/login").permitAll()
                        .pathMatchers("/health").permitAll()
                        .pathMatchers("/h2-console/**").permitAll()

                        // Endpoints administrativos (requieren autenticación)
                        .pathMatchers("/admin/**").authenticated()

                        // Cualquier otra ruta requiere autenticación
                        .anyExchange().authenticated()
                )

                // Deshabilitar form login (no necesario para APIs)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // HTTP Basic (opcional, para endpoints admin)
                .httpBasic(basic -> {})

                .build();
    }
}