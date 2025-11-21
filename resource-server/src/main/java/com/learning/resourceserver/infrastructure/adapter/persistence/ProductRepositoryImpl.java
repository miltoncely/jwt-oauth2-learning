package com.learning.resourceserver.infrastructure.adapter.persistence;

import com.learning.resourceserver.domain.model.Product;
import com.learning.resourceserver.domain.repository.ProductRepository;
import com.learning.resourceserver.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProductRepositoryImpl implements ProductRepository {

    // Simulating a database for now to avoid complex DB setup in this learning project
    // In a real scenario, we would inject a ReactiveCrudRepository here
    private final Map<Long, ProductEntity> db = new ConcurrentHashMap<>();

    public ProductRepositoryImpl() {
        // Seed data
        db.put(1L, new ProductEntity(1L, "Laptop", "High performance laptop", new BigDecimal("1200.00")));
        db.put(2L, new ProductEntity(2L, "Mouse", "Wireless mouse", new BigDecimal("25.00")));
        db.put(3L, new ProductEntity(3L, "Keyboard", "Mechanical keyboard", new BigDecimal("80.00")));
    }

    @Override
    public Flux<Product> findAll() {
        return Flux.fromIterable(db.values())
                .map(this::toDomain);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return Mono.justOrEmpty(db.get(id))
                .map(this::toDomain);
    }

    private Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .build();
    }
}
