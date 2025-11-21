package com.learning.keygen.generator;

import lombok.Builder;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Builder
public class KeyPairResult {

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private Integer keySize;

    private String algorithm;

    private Long generatedAt;

    private String privateKeyPEM;

    private String publicKeyPEM;
}