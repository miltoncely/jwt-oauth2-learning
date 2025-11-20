package com.learning.resourceserver.application.usecase;

import com.learning.resourceserver.domain.model.Order;
import com.learning.resourceserver.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GetUserOrdersUseCase {
    private final OrderRepository orderRepository;

    public Flux<Order> execute(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
