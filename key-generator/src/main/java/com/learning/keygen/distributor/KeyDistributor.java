package com.learning.keygen.distributor;

import com.learning.keygen.config.KeyConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeyDistributor {

    private final KeyConfiguration keyConfiguration;

    public void distributeKeys() {
        if (!keyConfiguration.getDistribution().getEnabled()) {
            log.warn("Distribucion de claves DESHABILITADA en configuracion");
            return;
        }

        log.info("Iniciando distribucion de claves...");

        List<KeyConfiguration.Target> targets = keyConfiguration.getDistribution().getTargets();

        if (targets == null || targets.isEmpty()) {
            log.warn("No hay destinos configurados para distribucion");
            return;
        }

        int successCount = 0;
        int failureCount = 0;

        for (KeyConfiguration.Target target : targets) {
            try {
                distributeKeyToTarget(target);
                successCount++;
            } catch (Exception e) {
                log.error("Error distribuyendo clave al modulo '{}': {}",
                        target.getModule(), e.getMessage());
                failureCount++;
            }
        }

        log.info("Distribucion completada: {} exitosas, {} fallidas",
                successCount, failureCount);
    }

    private void distributeKeyToTarget(KeyConfiguration.Target target) throws IOException {
        log.info("Distribuyendo a modulo '{}'", target.getModule());
        log.debug("Tipo de clave: {}, Destino: {}", target.getKeyType(), target.getDestination());

        Path sourceKeyPath = getSourceKeyPath(target.getKeyType());
        Path destinationPath = Paths.get(target.getDestination());

        createDestinationDirectory(destinationPath);

        String filename = getKeyFilename(target.getKeyType());
        Path destinationFilePath = destinationPath.resolve(filename);

        Files.copy(
                sourceKeyPath,
                destinationFilePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        log.info("Clave {} copiada a: {}",
                target.getKeyType(), destinationFilePath.toAbsolutePath());

        verifyDistribution(destinationFilePath);
    }

    private Path getSourceKeyPath(String keyType) {
        String basePath = keyConfiguration.getOutput().getBasePath();
        String filename = "private".equalsIgnoreCase(keyType)
                ? keyConfiguration.getOutput().getPrivateKeyFilename()
                : keyConfiguration.getOutput().getPublicKeyFilename();

        return Paths.get(basePath, filename);
    }

    private String getKeyFilename(String keyType) {
        return "private".equalsIgnoreCase(keyType)
                ? keyConfiguration.getOutput().getPrivateKeyFilename()
                : keyConfiguration.getOutput().getPublicKeyFilename();
    }

    private void createDestinationDirectory(Path destinationPath) throws IOException {
        if (!Files.exists(destinationPath)) {
            log.debug("Creando directorio destino: {}", destinationPath);
            Files.createDirectories(destinationPath);
        }
    }

    private void verifyDistribution(Path filePath) {
        if (Files.exists(filePath)) {
            try {
                long fileSize = Files.size(filePath);
                log.debug("Verificacion exitosa: {} bytes", fileSize);
            } catch (IOException e) {
                log.warn("No se pudo verificar el tamaño del archivo");
            }
        } else {
            log.error("El archivo no existe despues de la distribucion");
        }
    }

    public void cleanDistributedKeys() {
        log.info("Limpiando claves distribuidas...");

        List<KeyConfiguration.Target> targets = keyConfiguration.getDistribution().getTargets();

        if (targets == null || targets.isEmpty()) {
            log.info("No hay destinos configurados");
            return;
        }

        for (KeyConfiguration.Target target : targets) {
            try {
                String filename = getKeyFilename(target.getKeyType());
                Path filePath = Paths.get(target.getDestination(), filename);

                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("Eliminada: {}", filePath);
                }
            } catch (IOException e) {
                log.error("Error eliminando clave del modulo '{}': {}",
                        target.getModule(), e.getMessage());
            }
        }

        log.info("Limpieza completada");
    }
}