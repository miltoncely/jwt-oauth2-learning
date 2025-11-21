package com.learning.keygen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "key-generator")
public class KeyConfiguration {

    private Integer keySize = 4096;

    private String algorithm = "RSA";

    private Output output = new Output();

    private Distribution distribution = new Distribution();

    @Data
    public static class Output {
        private String basePath = "./generated-keys";
        private String privateKeyFilename = "private_key.pem";
        private String publicKeyFilename = "public_key.pem";
    }

    @Data
    public static class Distribution {
        private Boolean enabled = true;
        private List<Target> targets = List.of();
    }

    @Data
    public static class Target {
        private String module;
        private String keyType;
        private String destination;
    }
}