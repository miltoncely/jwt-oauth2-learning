package com.learning.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initDatabase(R2dbcEntityTemplate template, UserRepository repository) {
        return args -> {
            String schema = "CREATE TABLE IF NOT EXISTS users (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "username VARCHAR(255), " +
                    "password VARCHAR(255), " +
                    "roles VARCHAR(255))";

            template.getDatabaseClient().sql(schema).fetch().rowsUpdated()
                    .then(repository.deleteAll())
                    .then(repository.save(new User(null, "admin", "{noop}123456", "ADMIN,USER")))
                    .then(repository.save(new User(null, "user", "{noop}password", "USER")))
                    .subscribe(
                            null,
                            error -> System.err.println("Error initializing DB: " + error.getMessage()),
                            () -> System.out.println("Database initialized with default users.")
                    );
        };
    }
}
