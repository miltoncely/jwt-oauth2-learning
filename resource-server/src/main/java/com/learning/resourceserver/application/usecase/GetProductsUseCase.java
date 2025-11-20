package com.learning.resourceserver.application.usecase;

import com.learning.resourceserver.domain.model.Product;
import com.learning.resourceserver.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GetProductsUseCase {
    private final ProductRepository productRepository;

    public Flux<Product> execute() {
        return productRepository.findAll();
    }
}
