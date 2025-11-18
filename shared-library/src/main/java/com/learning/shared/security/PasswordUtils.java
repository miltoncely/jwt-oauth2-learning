package com.learning.shared.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidades para trabajar con contraseñas.
 * Usa BCrypt para hashear y verificar contraseñas de forma segura.
 */
public final class PasswordUtils {

    private PasswordUtils() {
        // Clase de utilidad, no debe instanciarse
    }

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    public static boolean matches(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }

    public static void printHashedPassword(String plainPassword) {
        String hashed = hashPassword(plainPassword);
        System.out.println("Password: " + plainPassword);
        System.out.println("Hashed:   " + hashed);
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("BCrypt Password Hash Generator");
        System.out.println("==============================");
        System.out.println();

        printHashedPassword("admin123");
        printHashedPassword("user123");
        printHashedPassword("password");
    }
}