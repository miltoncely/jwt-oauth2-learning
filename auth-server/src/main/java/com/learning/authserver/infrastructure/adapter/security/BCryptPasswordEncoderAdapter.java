package com.learning.authserver.infrastructure.adapter.security;

import com.learning.authserver.application.port.PasswordEncoder;
import com.learning.shared.security.PasswordUtils;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        return PasswordUtils.hashPassword(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return PasswordUtils.matches(rawPassword, encodedPassword);
    }
}