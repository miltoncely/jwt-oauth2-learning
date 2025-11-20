package com.learning.authserver.infrastructure.adapter.persistence.repository;

import com.learning.authserver.infrastructure.adapter.persistence.entity.ClientEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ClientR2dbcRepository extends R2dbcRepository<ClientEntity, Long> {

    Mono<ClientEntity> findByClientId(String clientId);
}