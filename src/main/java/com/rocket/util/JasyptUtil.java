package com.rocket.util;

import org.jasypt.util.text.BasicTextEncryptor;


public class JasyptUtil {

    private static BasicTextEncryptor textEncryptor = new BasicTextEncryptor();

    static {
        textEncryptor.setPassword("rocketChrome");
    }

    /**
     * 根据salt密钥加密
     * @param salt 加盐 盐值
     * @param str 需要被加密的字符串
     * @return 加密后的字符串
     */
    public static String getEncrypt(String salt, String str) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword(salt);
        return textEncryptor.encrypt(str);
    }

    /**
     * 根据salt密钥解密
     * @param salt 加盐 盐值
     * @param str 需要被加密的字符串
     * @return 加密后的字符串
     */
    public static String getDecrypt(String salt, String str) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword(salt);
        return textEncryptor.decrypt(str);
    }

    /**
     * 固定加密 加盐 盐值:lingying.sha1
     * @param str 需要被加密的字符串
     * @return 加密后的字符串
     */
    public static String getEncrypt(String str) {
        return textEncryptor.encrypt(str);
    }

    /**
     * 固定解密 加盐 盐值:lingying.sha1
     * @param str 需要被解密的字符串
     * @return 加密后的字符串
     */
    public static String getDecrypt(String str) {
        return textEncryptor.decrypt(str);
    }

}
