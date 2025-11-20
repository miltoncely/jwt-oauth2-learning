package com.learning.resourceserver.infrastructure.adapter.persistence;

import com.learning.resourceserver.domain.model.Order;
import com.learning.resourceserver.domain.repository.OrderRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final Map<Long, Order> db = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Mono<Order> save(Order order) {
        long id = idGenerator.getAndIncrement();
        order.setId(id);
        db.put(id, order);
        return Mono.just(order);
    }

    @Override
    public Flux<Order> findByUserId(String userId) {
        return Flux.fromIterable(db.values())
                .filter(order -> order.getUserId().equals(userId));
    }
}
