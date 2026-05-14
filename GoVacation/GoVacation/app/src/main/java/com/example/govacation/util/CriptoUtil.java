package com.example.govacation.util;

import java.security.MessageDigest;

public class CriptoUtil {

    private CriptoUtil() {
    }

    public static String hashSHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(texto.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro crítico ao gerar hash SHA-256.", e);
        }
    }
}
