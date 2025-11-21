package com.learning.resource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class PublicKeyLoader {

    public RSAPublicKey loadPublicKey() {
        try {
            ClassPathResource resource = new ClassPathResource("keys/public.pem");
            try (InputStream inputStream = resource.getInputStream()) {
                String keyContent = new String(inputStream.readAllBytes())
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] keyBytes = Base64.getDecoder().decode(keyContent);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                return (RSAPublicKey) kf.generatePublic(spec);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key", e);
        }
    }
}
