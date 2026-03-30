package com.rocket.util;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 非对称加密工具类（RSA）
 * 用于服务端和客户端之间的加密通信
 * 服务端使用私钥加密，客户端使用公钥解密
 */
public class AsymmetricCryptoUtil {
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final long EXPIRATION_TIME = 5 * 60 * 1000;

    private String getPublicKey() {
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuSGOX6C+yilig7/Ntq4QiHi50CE6DxpwzTfX9Y9ZUSwH+RxWHTmJGbD7mhv38mMUG78WIQrMDtpruZ/IjJhBQSMwxBHqSlfeNLTYVVwfnpUp4KprI/R6p85oiBzSl2i+evQmU5PgZBNSg3spRpxL00jqtuN9ZRYNIJxorIjWeGcuumRhgsImypjCrzHlzpFXjIMJX28fjaZu0S8EPFub09x2iH+xlcBXmY8q25jCXb2le9omPFzeTDvCR01NV2o2jIPbQXY9qDMWRUPsbK4jTrDQZKK2pI3217DaBDodGP4Cw6+Txc7Ci7/V3XQh7V9j5GDo/axVTf1dmBsuP1RoKQIDAQAB";
    }

    /**
     * 生成RSA密钥对
     * @return 包含公钥和私钥的Map，键分别为"publicKey"和"privateKey"
     * @throws NoSuchAlgorithmException 当指定的算法不存在时抛出
     */
    public static Map<String, String> generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 将密钥转换为Base64字符串
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("publicKey", publicKeyStr);
        keyMap.put("privateKey", privateKeyStr);
        return keyMap;
    }

    /**
     * 使用私钥加密数据（服务端使用）
     * @param data 待加密的数据
     * @param privateKeyStr 私钥的Base64字符串
     * @return 加密后的Base64字符串
     * @throws Exception 加密过程中的异常
     */
    public static String encryptWithPrivateKey(String data, String privateKeyStr) throws Exception {
        // 添加时间戳（毫秒）
        long timestamp = System.currentTimeMillis();
        String dataWithTimestamp = data + "|" + timestamp;
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedBytes = cipher.doFinal(dataWithTimestamp.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用公钥解密数据（客户端使用）
     * @param encryptedData 加密后的Base64字符串
     * @param publicKeyStr 公钥的Base64字符串
     * @return 解密后的明文
     * @throws Exception 解密过程中的异常
     */
    public static String decryptWithPublicKey(String encryptedData, String publicKeyStr) throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyStr);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // 解析数据和时间戳
        String decryptedStr = new String(decryptedBytes);
        int separatorIndex = decryptedStr.lastIndexOf("|");
        if (separatorIndex == -1) {
            throw new Exception("Invalid encrypted data format");
        }

        String data = decryptedStr.substring(0, separatorIndex);
        long timestamp = Long.parseLong(decryptedStr.substring(separatorIndex + 1));

        // 验证时间戳是否在有效期内
        if (!isTimestampValid(timestamp)) {
            throw new Exception("Encrypted data has expired");
        }

        return data;
    }

    /**
     * 验证时间戳是否在有效期内
     * @param timestamp 加密时的时间戳
     * @return 是否在有效期内
     */
    private static boolean isTimestampValid(long timestamp) {
        long currentTime = System.currentTimeMillis();
        return currentTime - timestamp <= EXPIRATION_TIME;
    }

    /**
     * 使用公钥加密数据
     * @param data 待加密的数据
     * @param publicKeyStr 公钥的Base64字符串
     * @return 加密后的Base64字符串
     * @throws Exception 加密过程中的异常
     */
    public static String encryptWithPublicKey(String data, String publicKeyStr) throws Exception {
        // 添加时间戳（毫秒）
        long timestamp = System.currentTimeMillis();
        String dataWithTimestamp = data + "|" + timestamp;

        PublicKey publicKey = getPublicKey(publicKeyStr);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(dataWithTimestamp.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用私钥解密数据
     * @param encryptedData 加密后的Base64字符串
     * @param privateKeyStr 私钥的Base64字符串
     * @return 解密后的明文
     * @throws Exception 解密过程中的异常
     */
    public static String decryptWithPrivateKey(String encryptedData, String privateKeyStr) throws Exception {
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // 解析数据和时间戳
        String decryptedStr = new String(decryptedBytes);
        int separatorIndex = decryptedStr.lastIndexOf("|");
        if (separatorIndex == -1) {
            throw new Exception("Invalid encrypted data format");
        }

        String data = decryptedStr.substring(0, separatorIndex);
        long timestamp = Long.parseLong(decryptedStr.substring(separatorIndex + 1));

        // 验证时间戳是否在有效期内
        if (!isTimestampValid(timestamp)) {
            throw new Exception("Encrypted data has expired");
        }

        return data;
    }

    /**
     * 将Base64字符串转换为公钥对象
     * @param publicKeyStr 公钥的Base64字符串
     * @return PublicKey对象
     * @throws Exception 转换过程中的异常
     */
    private static PublicKey getPublicKey(String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 将Base64字符串转换为私钥对象
     * @param privateKeyStr 私钥的Base64字符串
     * @return PrivateKey对象
     * @throws Exception 转换过程中的异常
     */
    private static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) throws Exception {
        // 生成密钥对
        Map<String, String> keyMap = generateKeyPair();
        String publicKey = keyMap.get("publicKey");
        String privateKey = keyMap.get("privateKey");

        System.out.println("公钥：" + publicKey);
        System.out.println("私钥：" + privateKey);

        // 测试数据
        String originalData = "Hello, Asymmetric Encryption!";
        System.out.println("原始数据：" + originalData);

        // 服务端使用私钥加密
        String encryptedData = encryptWithPrivateKey(originalData, privateKey);
        System.out.println("加密后数据：" + encryptedData);

        // 客户端使用公钥解密
        String decryptedData = decryptWithPublicKey(encryptedData, publicKey);
        System.out.println("解密后数据：" + decryptedData);

        // 验证解密是否正确
        System.out.println("解密是否正确：" + originalData.equals(decryptedData));
    }
}
