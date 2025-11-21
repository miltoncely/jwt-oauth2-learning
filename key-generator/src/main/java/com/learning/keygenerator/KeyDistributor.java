package com.learning.keygenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class KeyDistributor {

    public void saveKey(String content, String pathStr) {
        try {
            Path path = Paths.get(pathStr);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Key saved to: " + path.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save key to " + pathStr, e);
        }
    }
}
