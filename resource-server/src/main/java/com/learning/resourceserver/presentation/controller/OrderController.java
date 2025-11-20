package com.learning.resourceserver.presentation.controller;

import com.learning.resourceserver.application.usecase.CreateOrderUseCase;
import com.learning.resourceserver.application.usecase.GetUserOrdersUseCase;
import com.learning.resourceserver.domain.model.Order;
import com.learning.resourceserver.domain.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetUserOrdersUseCase getUserOrdersUseCase;

    @PostMapping
    public Mono<Order> createOrder(@RequestBody Order order, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        order.setUserId(userPrincipal.getId());
        return createOrderUseCase.execute(order);
    }

    @GetMapping
    public Flux<Order> getMyOrders(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return getUserOrdersUseCase.execute(userPrincipal.getId());
    }
}
