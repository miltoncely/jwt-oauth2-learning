package com.learning.authserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Slf4j
@SpringBootApplication
@EnableR2dbcRepositories
public class AuthServerApplication {

    public static void main(String[] args) {
        log.info("AUTHORIZATION SERVER - Starting...");

        SpringApplication.run(AuthServerApplication.class, args);

        log.info("Authorization Server iniciado exitosamente");
    }
}