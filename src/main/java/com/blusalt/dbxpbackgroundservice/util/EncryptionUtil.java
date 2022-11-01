package com.blusalt.dbxpbackgroundservice.util;

import com.blusalt.commons.exceptions.DbxpApplicationException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import static com.blusalt.dbxpbackgroundservice.util.RandomIdGenerator.generateRandomId;

@Slf4j
public class EncryptionUtil {
    private static String mySecret = "ooeihgi78yhdheuh873ihu7ghdygyuwihh8ggytfcggd8";

    public static SecretKey getSecretKey(String secret) {
        try {
            MessageDigest sha = null;

            byte[] key = secret.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            return secretKey;

        } catch (Exception e) {
            throw new DbxpApplicationException("unable to generate key: " + e.getMessage());
        }
    }

    public static String encrypt(final String strToEncrypt, final String secret) {
        try {
            SecretKey secretKey = getSecretKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new DbxpApplicationException("unable to generate key");
        }

    }

    public static String decrypt(final String strToDecrypt, final String secret) {
        try {
            SecretKey secretKey = getSecretKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder()
                    .decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static String encryptSecureSecret(String secureSecret) {
        return encrypt(secureSecret, mySecret);
    }

    public static String decryptSecureSecret(String encryptedSecret) {
        return decrypt(encryptedSecret, mySecret);
    }

    public static String generateSecureKey() {
        return generateRandomId(32);
    }


}
