package com.digitalmoney.msaccounts.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Service
public class CVVService {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;

    public static String cryptCVV(String cvv, String key) throws Exception {
        SecretKey secretKey = generateAESKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedCVV = cipher.doFinal(cvv.getBytes());
        return Base64.getEncoder().encodeToString(encryptedCVV);
    }

    private static SecretKey generateAESKey(String key) throws NoSuchAlgorithmException {
        byte[] keyBytes = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), KEY_SIZE / 8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
