package cn.hanabi.irc.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

public class DESUtil {

    //AES更好用
    private final static String DES = "AES";
    //记得修改密钥
    private final static String defaultKey = initKey("QWEQWEQAWEFWWEFH1A3N2A81B890I...");

    private static Key toKey(byte[] key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, DES);
            return secretKey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String initKey(String seed) {
        try {
            SecureRandom secureRandom = null;
            secureRandom = seed != null ? new SecureRandom(decryptBASE64(seed)) : new SecureRandom();
            KeyGenerator kg = KeyGenerator.getInstance(DES);
            kg.init(secureRandom);
            SecretKey secretKey = kg.generateKey();
            return encryptBASE64(secretKey.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decryptBASE64(String key) {
        try {
            return new BASE64Decoder().decodeBuffer(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encryptBASE64(byte[] key) {
        try {
            return new BASE64Encoder().encodeBuffer(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(byte[] data) {
        return new String(DESUtil.encrypt(data, defaultKey));
    }

    public static byte[] decrypt(String data) {
        if (data == null)
            return null;
        return DESUtil.decrypt(data, defaultKey);
    }

    /**
     * Description 根据键值进行加密
     *
     * @param data 加密数据
     * @param key  密钥
     * @return
     * @throws Exception
     */

    public static byte[] encrypt(byte[] data, String key) {
        try {
            Key k = toKey(decryptBASE64(key));
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(1, k);
            return encryptBASE64(cipher.doFinal(data)).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data 密文
     * @param key  密钥
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(String data, String key) {
        try {
            Key k = toKey(decryptBASE64(key));
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(2, k);
            byte[] bytes = decryptBASE64(data);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
