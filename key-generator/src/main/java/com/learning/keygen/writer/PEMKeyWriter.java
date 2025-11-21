package com.learning.keygen.writer;

import com.learning.keygen.generator.KeyPairResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@Component
public class PEMKeyWriter {

    public void writeKeyPair(
            KeyPairResult keyPairResult,
            String basePath,
            String privateKeyFilename,
            String publicKeyFilename
    ) throws IOException {

        log.info("Guardando claves en formato PEM...");
        log.info("Ruta base: {}", basePath);

        Path baseDirectory = Paths.get(basePath);
        createDirectoryIfNotExists(baseDirectory);

        Path privateKeyPath = baseDirectory.resolve(privateKeyFilename);
        Path publicKeyPath = baseDirectory.resolve(publicKeyFilename);

        writeKeyToFile(
                privateKeyPath,
                keyPairResult.getPrivateKeyPEM(),
                "PRIVADA"
        );

        writeKeyToFile(
                publicKeyPath,
                keyPairResult.getPublicKeyPEM(),
                "PUBLICA"
        );

        log.info("Claves guardadas exitosamente");
        log.info("Clave privada: {}", privateKeyPath.toAbsolutePath());
        log.info("Clave publica: {}", publicKeyPath.toAbsolutePath());
    }

    private void writeKeyToFile(Path path, String content, String keyType) throws IOException {
        log.debug("Escribiendo clave {}...", keyType);

        Files.writeString(
                path,
                content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        log.debug("Clave {} guardada ({} bytes)", keyType, content.length());
    }

    private void createDirectoryIfNotExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            log.debug("Creando directorio: {}", directory);
            Files.createDirectories(directory);
        } else {
            log.debug("Directorio ya existe: {}", directory);
        }
    }

    public boolean verifyKeysExist(
            String basePath,
            String privateKeyFilename,
            String publicKeyFilename
    ) {
        Path privateKeyPath = Paths.get(basePath, privateKeyFilename);
        Path publicKeyPath = Paths.get(basePath, publicKeyFilename);

        boolean privateExists = Files.exists(privateKeyPath);
        boolean publicExists = Files.exists(publicKeyPath);

        log.debug("Verificacion de archivos:");
        log.debug("Clave privada: {} ({})", privateKeyPath, privateExists ? "EXISTE" : "NO EXISTE");
        log.debug("Clave publica: {} ({})", publicKeyPath, publicExists ? "EXISTE" : "NO EXISTE");

        return privateExists && publicExists;
    }
}