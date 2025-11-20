package com.learning.authserver.domain.repository;

import com.learning.authserver.domain.model.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRepository {
    Mono<Client> findById(Long id);
    Mono<Client> findByClientId(String clientId);
    Mono<Client> save(Client client);
    Flux<Client> findAll();
    Mono<Void> deleteById(Long id);
}