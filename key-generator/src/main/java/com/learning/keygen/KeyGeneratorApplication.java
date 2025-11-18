package com.learning.keygen;

import com.learning.keygen.config.KeyConfiguration;
import com.learning.keygen.distributor.KeyDistributor;
import com.learning.keygen.generator.KeyPairResult;
import com.learning.keygen.generator.RSAKeyPairGenerator;
import com.learning.keygen.writer.PEMKeyWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class KeyGeneratorApplication implements CommandLineRunner {

    private final RSAKeyPairGenerator keyPairGenerator;
    private final PEMKeyWriter keyWriter;
    private final KeyDistributor keyDistributor;
    private final KeyConfiguration keyConfiguration;

    public static void main(String[] args) {
        log.info("===============================================");
        log.info("JWT/OAuth2 - Generador de Claves RSA");
        log.info("===============================================");
        SpringApplication.run(KeyGeneratorApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            log.info("INICIANDO PROCESO DE GENERACION DE CLAVES");
            log.info("==========================================");

            KeyPairResult keyPairResult = generateKeys();
            validateKeys(keyPairResult);
            saveKeys(keyPairResult);
            distributeKeys();

            log.info("PROCESO COMPLETADO EXITOSAMENTE");
            log.info("===============================");

            printSummary(keyPairResult);

        } catch (Exception e) {
            log.error("");
            log.error("ERROR EN EL PROCESO");
            log.error("===================");
            log.error("");
            log.error("Error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private KeyPairResult generateKeys() throws Exception {
        int keySize = keyConfiguration.getKeySize();
        KeyPairResult result = keyPairGenerator.generateKeyPair(keySize);

        log.info("Claves generadas correctamente");
        log.info("Algoritmo: {}, Tamaño: {} bits", result.getAlgorithm(), result.getKeySize());
        log.info("Timestamp: {}", result.getGeneratedAt());

        return result;
    }

    private void validateKeys(KeyPairResult keyPairResult) {
        boolean isValid = keyPairGenerator.validateKeyPair(keyPairResult);

        if (!isValid) {
            throw new RuntimeException("Las claves generadas no son validas");
        }

        log.info("Par de claves validado exitosamente");
    }

    private void saveKeys(KeyPairResult keyPairResult) throws Exception {
        String basePath = keyConfiguration.getOutput().getBasePath();
        String privateFilename = keyConfiguration.getOutput().getPrivateKeyFilename();
        String publicFilename = keyConfiguration.getOutput().getPublicKeyFilename();

        keyWriter.writeKeyPair(
                keyPairResult,
                basePath,
                privateFilename,
                publicFilename
        );

        boolean exists = keyWriter.verifyKeysExist(basePath, privateFilename, publicFilename);

        if (!exists) {
            throw new RuntimeException("No se pudieron crear los archivos de claves");
        }

        log.info("Claves guardadas en: {}", basePath);
    }

    private void distributeKeys() {
        keyDistributor.distributeKeys();
        log.info("Distribucion completada");
    }

    private void printSummary(KeyPairResult keyPairResult) {
        log.info("RESUMEN:");
        log.info("--------");
        log.info("Claves RSA de {} bits generadas", keyPairResult.getKeySize());
        log.info("Clave privada -> {}/{}",
                keyConfiguration.getOutput().getBasePath(),
                keyConfiguration.getOutput().getPrivateKeyFilename());
        log.info("Clave publica -> {}/{}",
                keyConfiguration.getOutput().getBasePath(),
                keyConfiguration.getOutput().getPublicKeyFilename());

        if (keyConfiguration.getDistribution().getEnabled()) {
            log.info("Claves distribuidas a {} modulo(s)",
                    keyConfiguration.getDistribution().getTargets().size());

            keyConfiguration.getDistribution().getTargets().forEach(target -> {
                log.info("   {} ({}) -> {}",
                        target.getModule(),
                        target.getKeyType().toUpperCase(),
                        target.getDestination());
            });
        }
    }
}