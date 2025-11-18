package com.learning.keygenerator;

import java.security.KeyPair;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Key Generation...");

        RsaKeyGenerator generator = new RsaKeyGenerator();
        PemExporter exporter = new PemExporter();
        KeyDistributor distributor = new KeyDistributor();

        KeyPair keyPair = generator.generateKeyPair();

        String privateKeyPem = exporter.exportToPem(keyPair.getPrivate(), "PRIVATE KEY");
        String publicKeyPem = exporter.exportToPem(keyPair.getPublic(), "PUBLIC KEY");

        // Auth Service needs Private Key (to sign) and Public Key (optional, but good for verification)
        distributor.saveKey(privateKeyPem, "auth-service/src/main/resources/keys/private.pem");
        distributor.saveKey(publicKeyPem, "auth-service/src/main/resources/keys/public.pem");

        // Resource Service needs Public Key (to verify)
        distributor.saveKey(publicKeyPem, "resource-service/src/main/resources/keys/public.pem");

        System.out.println("Keys generated and distributed successfully.");
    }
}
