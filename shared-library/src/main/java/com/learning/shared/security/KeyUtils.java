package com.learning.shared.security;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public final class KeyUtils {

    private KeyUtils() {
        // Clase de utilidad, no debe instanciarse
    }

    private static final String PRIVATE_KEY_PREFIX = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_SUFFIX = "-----END PRIVATE KEY-----";
    private static final String PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_SUFFIX = "-----END PUBLIC KEY-----";

    public static PrivateKey loadPrivateKey(String keyPath) throws Exception {
        log.debug("Cargando clave privada desde: {}", keyPath);

        String keyContent = Files.readString(Path.of(keyPath));

        String privateKeyPEM = keyContent
                .replace(PRIVATE_KEY_PREFIX, "")
                .replace(PRIVATE_KEY_SUFFIX, "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        log.debug("Clave privada cargada exitosamente");
        return privateKey;
    }

    public static PublicKey loadPublicKey(String keyPath) throws Exception {
        log.debug("Cargando clave publica desde: {}", keyPath);

        String keyContent = Files.readString(Path.of(keyPath));

        String publicKeyPEM = keyContent
                .replace(PUBLIC_KEY_PREFIX, "")
                .replace(PUBLIC_KEY_SUFFIX, "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        log.debug("Clave publica cargada exitosamente");
        return publicKey;
    }

    public static PrivateKey loadPrivateKeyFromClasspath(String resourcePath) throws Exception {
        log.debug("Cargando clave privada desde classpath: {}", resourcePath);

        var resource = KeyUtils.class.getClassLoader().getResourceAsStream(resourcePath);

        if (resource == null) {
            throw new IllegalArgumentException("No se encontro el recurso: " + resourcePath);
        }

        String keyContent = new String(resource.readAllBytes());

        String privateKeyPEM = keyContent
                .replace(PRIVATE_KEY_PREFIX, "")
                .replace(PRIVATE_KEY_SUFFIX, "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        log.debug("Clave privada cargada desde classpath");
        return privateKey;
    }

    public static PublicKey loadPublicKeyFromClasspath(String resourcePath) throws Exception {
        log.debug("Cargando clave publica desde classpath: {}", resourcePath);

        var resource = KeyUtils.class.getClassLoader().getResourceAsStream(resourcePath);

        if (resource == null) {
            throw new IllegalArgumentException("No se encontro el recurso: " + resourcePath);
        }

        String keyContent = new String(resource.readAllBytes());

        String publicKeyPEM = keyContent
                .replace(PUBLIC_KEY_PREFIX, "")
                .replace(PUBLIC_KEY_SUFFIX, "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        log.debug("Clave publica cargada desde classpath");
        return publicKey;
    }

    public static boolean verifyKeyPair(PrivateKey privateKey, PublicKey publicKey) {
        try {
            String testData = "Test data for key verification";
            byte[] dataBytes = testData.getBytes();

            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(dataBytes);
            byte[] signatureBytes = signature.sign();

            signature.initVerify(publicKey);
            signature.update(dataBytes);
            boolean isValid = signature.verify(signatureBytes);

            if (isValid) {
                log.debug("Par de claves verificado correctamente");
            } else {
                log.error("El par de claves NO coincide");
            }

            return isValid;

        } catch (Exception e) {
            log.error("Error verificando par de claves: {}", e.getMessage());
            return false;
        }
    }

    public static String getKeyInfo(java.security.Key key) {
        return String.format(
                "Algorithm: %s, Format: %s, Encoded Length: %d bytes",
                key.getAlgorithm(),
                key.getFormat(),
                key.getEncoded().length
        );
    }
}