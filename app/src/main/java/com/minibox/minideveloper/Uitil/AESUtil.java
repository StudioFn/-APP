package com.minibox.minideveloper.Uitil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class AESUtil {

    static final BASE64Encoder encoder = new BASE64Encoder();
    static final BASE64Decoder decoder = new BASE64Decoder();
    private static final byte[] IV = "t1ivk4o9t1ivk4o9".getBytes(StandardCharsets.UTF_8);
    private AESUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] encrypt(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String data, String key) {
        return encoder.encode(encrypt(data.getBytes(), key.getBytes()));
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), iv);
            return cipher.doFinal(data);
        } catch (Exception ignored) {

        }
        return null;
    }

    public static String decrypt(String data, String key) throws IOException {
        return new String(Objects.requireNonNull(decrypt(decoder.decodeBuffer(data), key.getBytes())));
    }

}
