package com.learning.resourceserver.domain.repository;

import com.learning.resourceserver.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Flux<Product> findAll();
    Mono<Product> findById(Long id);
}
