package com.learning.auth.security;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class PrivateKeyLoader {

    public RSAPrivateKey loadPrivateKey() {
        try {
            ClassPathResource resource = new ClassPathResource("keys/private.pem");
            try (InputStream inputStream = resource.getInputStream()) {
                String keyContent = new String(inputStream.readAllBytes())
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] keyBytes = Base64.getDecoder().decode(keyContent);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                return (RSAPrivateKey) kf.generatePrivate(spec);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }
}
