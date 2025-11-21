package com.learning.resourceserver.domain.repository;

import com.learning.resourceserver.domain.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {
    Mono<Order> save(Order order);
    Flux<Order> findByUserId(String userId);
}
