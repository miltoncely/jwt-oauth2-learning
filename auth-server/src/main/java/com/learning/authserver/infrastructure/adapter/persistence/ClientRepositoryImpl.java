package com.learning.authserver.infrastructure.adapter.persistence;

import com.learning.authserver.domain.model.Client;
import com.learning.authserver.domain.repository.ClientRepository;
import com.learning.authserver.infrastructure.adapter.persistence.entity.ClientEntity;
import com.learning.authserver.infrastructure.adapter.persistence.repository.ClientR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Client> findById(Long id) {
        log.debug("Buscando cliente por ID: {}", id);
        return r2dbcRepository.findById(id)
                .map(ClientEntity::toDomain);
    }

    @Override
    public Mono<Client> findByClientId(String clientId) {
        log.debug("Buscando cliente por clientId: {}", clientId);
        return r2dbcRepository.findByClientId(clientId)
                .map(ClientEntity::toDomain);
    }

    @Override
    public Mono<Client> save(Client client) {
        log.debug("Guardando cliente: {}", client.getClientId());
        ClientEntity entity = ClientEntity.fromDomain(client);
        return r2dbcRepository.save(entity)
                .map(ClientEntity::toDomain);
    }

    @Override
    public Flux<Client> findAll() {
        log.debug("Obteniendo todos los clientes");
        return r2dbcRepository.findAll()
                .map(ClientEntity::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.debug("Eliminando cliente por ID: {}", id);
        return r2dbcRepository.deleteById(id);
    }
}