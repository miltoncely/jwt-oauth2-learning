package com.learning.authserver.infrastructure.adapter.security;

import com.learning.shared.security.KeyUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;

@Slf4j
@Component
public class RSAKeyProvider {

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    private PrivateKey privateKey;

    @PostConstruct
    public void loadKey() {
        try {
            log.info("Iniciando carga de clave privada RSA...");
            log.debug("Ruta configurada: {}", privateKeyPath);

            String rutaLimpia = privateKeyPath.replace("classpath:", "");
            this.privateKey = KeyUtils.loadPrivateKeyFromClasspath(rutaLimpia);

            log.info("Clave privada RSA cargada correctamente");

        } catch (Exception e) {
            log.error("Fallo en la carga de la clave privada RSA", e);
            throw new RuntimeException("Error cargando clave privada RSA", e);
        }
    }

    public PrivateKey getPrivateKey() {
        if (privateKey == null) {
            throw new IllegalStateException("La clave privada no esta disponible");
        }
        return privateKey;
    }

    public boolean isEnabled() {
        return privateKey != null;
    }
}