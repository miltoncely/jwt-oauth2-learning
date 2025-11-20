package com.learning.resourceserver.application.port;

import com.learning.resourceserver.domain.model.UserPrincipal;
import reactor.core.publisher.Mono;

public interface TokenValidator {
    Mono<UserPrincipal> validate(String token);
}
