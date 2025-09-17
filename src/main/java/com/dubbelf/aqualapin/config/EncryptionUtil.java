package com.dubbelf.aqualapin.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "2W75cS3O6SwcK7k$";
    private static final String IV = "RandomInitVector"; // 16 bytes IV for AES

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public String decrypt(String encryptedText) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedVal = cipher.doFinal(decodedValue);
            return new String(decryptedVal, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting", e);
        }
    }
}
