package com.learning.keygenerator;

import java.security.Key;
import java.util.Base64;

public class PemExporter {

    public String exportToPem(Key key, String description) {
        String encoded = Base64.getMimeEncoder(64, new byte[] { '\n' }).encodeToString(key.getEncoded());
        return "-----BEGIN " + description + "-----\n" +
               encoded +
               "\n-----END " + description + "-----\n";
    }
}