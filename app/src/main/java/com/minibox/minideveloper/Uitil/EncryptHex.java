/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Uitil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptHex {
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String LK = "e82ckenh8dichen8";

    // 将字节数组转换为16进制字符串
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    // AES加密，返回16进制字符串
    public static String encrypt(String data) {
        byte[] encryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION); // 创建密码器
            SecretKeySpec secretKeySpec = new SecretKeySpec(LK.getBytes(), "AES"); // 根据密钥创建密钥规范
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // 初始化密码器为加密模式
            encryptedBytes = cipher.doFinal(data.getBytes()); // 对数据进行加密
        } catch (NoSuchPaddingException
                 | NoSuchAlgorithmException
                 | InvalidKeyException
                 | IllegalBlockSizeException
                 | BadPaddingException ignored) {
        }
        return bytesToHex(encryptedBytes); // 转换为16进制字符串
    }


}
