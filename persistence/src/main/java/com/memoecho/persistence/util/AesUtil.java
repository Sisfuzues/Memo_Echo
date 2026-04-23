package com.memoecho.persistence.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j(topic = "AES加密工具")
@Component
public class AesUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int KEY_LENGTH = 32;
    
    @Value("${security.aes.key}")
    private String aesKey;

    private SecretKey getSecretKey() {
        try {
            byte[] keyBytes = aesKey.getBytes(StandardCharsets.UTF_8);
            
            if (keyBytes.length < KEY_LENGTH) {
                log.warn("AES密钥长度不足{}位，当前长度: {}，将进行填充", KEY_LENGTH, keyBytes.length);
                byte[] adjustedKey = new byte[KEY_LENGTH];
                System.arraycopy(keyBytes, 0, adjustedKey, 0, keyBytes.length);
                keyBytes = adjustedKey;
            } else if (keyBytes.length > KEY_LENGTH) {
                log.warn("AES密钥长度超过{}位，当前长度: {}，将进行截断", KEY_LENGTH, keyBytes.length);
                byte[] adjustedKey = new byte[KEY_LENGTH];
                System.arraycopy(keyBytes, 0, adjustedKey, 0, KEY_LENGTH);
                keyBytes = adjustedKey;
            }
            
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            log.error("生成AES密钥失败", e);
            throw new RuntimeException("AES密钥生成失败，请检查配置项 security.aes.key", e);
        }
    }

    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("加密内容不能为空");
        }
        
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), parameterSpec);
            
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedBytes);
            
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("AES加密失败", e);
            throw new RuntimeException("数据加密失败", e);
        }
    }

    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("解密内容不能为空");
        }
        
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);
            
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            
            byte[] encryptedBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedBytes);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), parameterSpec);
            
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new RuntimeException("数据解密失败，可能是密钥不匹配或数据已损坏", e);
        }
    }
}
