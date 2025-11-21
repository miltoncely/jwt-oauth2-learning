package com.learning.authserver.infrastructure.adapter.persistence;

import com.learning.authserver.domain.model.User;
import com.learning.authserver.domain.repository.UserRepository;
import com.learning.authserver.infrastructure.adapter.persistence.entity.UserEntity;
import com.learning.authserver.infrastructure.adapter.persistence.repository.UserR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserR2dbcRepository r2dbcRepository;

    @Override
    public Mono<User> findById(Long id) {
        log.debug("Buscando usuario por ID: {}", id);
        return r2dbcRepository.findById(id)
                .map(UserEntity::toDomain);
    }

    @Override
    public Mono<User> findByUsername(String username) {
        log.debug("Buscando usuario por username: {}", username);
        return r2dbcRepository.findByUsername(username)
                .map(UserEntity::toDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        return r2dbcRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        log.debug("Guardando usuario: {}", user.getUsername());
        UserEntity entity = UserEntity.fromDomain(user);
        return r2dbcRepository.save(entity)
                .map(UserEntity::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.debug("Eliminando usuario por ID: {}", id);
        return r2dbcRepository.deleteById(id);
    }

    @Override
    public Flux<User> findAll() {
        log.debug("Obteniendo todos los usuarios");
        return r2dbcRepository.findAll()
                .map(UserEntity::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return r2dbcRepository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return r2dbcRepository.existsByEmail(email);
    }
}