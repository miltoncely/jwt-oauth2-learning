package com.learning.resourceserver.application.usecase;

import com.learning.resourceserver.domain.model.Order;
import com.learning.resourceserver.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final OrderRepository orderRepository;

    public Mono<Order> execute(Order order) {
        return orderRepository.save(order);
    }
}
