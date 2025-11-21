package com.learning.resourceserver.presentation.controller;

import com.learning.resourceserver.application.usecase.GetProductsUseCase;
import com.learning.resourceserver.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final GetProductsUseCase getProductsUseCase;

    @GetMapping
    public Flux<Product> getAllProducts() {
        return getProductsUseCase.execute();
    }
}
