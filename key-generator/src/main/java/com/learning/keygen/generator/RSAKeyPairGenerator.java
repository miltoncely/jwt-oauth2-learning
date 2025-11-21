package com.learning.keygen.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.*;
import java.util.Base64;

@Slf4j
@Component
public class RSAKeyPairGenerator {

    private static final String ALGORITHM = "RSA";

    public KeyPairResult generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        log.info("Iniciando generacion de par de claves RSA");
        log.info("Algoritmo: {}, Tamaño: {} bits", ALGORITHM, keySize);

        long startTime = System.currentTimeMillis();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        long endTime = System.currentTimeMillis();
        log.info("Par de claves generado exitosamente en {} ms", endTime - startTime);

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String privateKeyPEM = convertToPEM(privateKey, "PRIVATE KEY");
        String publicKeyPEM = convertToPEM(publicKey, "PUBLIC KEY");

        return KeyPairResult.builder()
                .privateKey(privateKey)
                .publicKey(publicKey)
                .keySize(keySize)
                .algorithm(ALGORITHM)
                .generatedAt(System.currentTimeMillis())
                .privateKeyPEM(privateKeyPEM)
                .publicKeyPEM(publicKeyPEM)
                .build();
    }

    private String convertToPEM(Key key, String type) {
        byte[] keyBytes = key.getEncoded();
        String base64Encoded = Base64.getEncoder().encodeToString(keyBytes);

        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN ").append(type).append("-----\n");

        int index = 0;
        while (index < base64Encoded.length()) {
            int endIndex = Math.min(index + 64, base64Encoded.length());
            pem.append(base64Encoded, index, endIndex).append("\n");
            index = endIndex;
        }

        pem.append("-----END ").append(type).append("-----\n");
        return pem.toString();
    }

    public boolean validateKeyPair(KeyPairResult keyPairResult) {
        log.info("Validando par de claves...");

        try {
            String testData = "Test data for key validation";
            byte[] dataBytes = testData.getBytes();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPairResult.getPrivateKey());
            signature.update(dataBytes);
            byte[] signatureBytes = signature.sign();

            log.debug("Firma generada: {} bytes", signatureBytes.length);

            signature.initVerify(keyPairResult.getPublicKey());
            signature.update(dataBytes);
            boolean isValid = signature.verify(signatureBytes);

            if (isValid) {
                log.info("Par de claves validado correctamente");
            } else {
                log.error("El par de claves NO es valido");
            }

            return isValid;

        } catch (Exception e) {
            log.error("Error durante la validacion del par de claves", e);
            return false;
        }
    }
}