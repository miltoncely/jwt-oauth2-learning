package com.learning.authserver.domain.repository;

import com.learning.authserver.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findById(Long id);
    Mono<User> findByUsername(String username);
    Mono<User> findByEmail(String email);
    Mono<User> save(User user);
    Mono<Void> deleteById(Long id);
    Flux<User> findAll();
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
}