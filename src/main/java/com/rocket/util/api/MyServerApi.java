package com.rocket.util.api;

import com.rocket.util.AsymmetricCryptoUtil;
import org.apache.http.HttpResponse;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cjl
 * @description
 * @project rocket-game-live-assistant
 * @dateTime 2024-02-19 13:21:11
 * @version 1.0
 */
public class MyServerApi {
    private static String code;
    private static String mac = null;
    private static String phone;

    /**
     * @author cjl
     * @description 登录(并激活卡密)
     * @dateTime 2024-02-19 13:02:17 (注释添加时间,包括但不完全代表创建或完成时间)
     * @param code java.lang.String
     * @param mac java.lang.String
     * @return java.lang.String
     */
    public static String login(String code, String mac, String phone) {
        // 获取本机的所有网络接口
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        if (MyServerApi.mac == null) {
            while (networkInterfaces.hasMoreElements()) {
                // 获取当前网络接口
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // 获取网络接口的mac地址
                byte[] mac_b;
                try {
                    mac_b = networkInterface.getHardwareAddress();
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
                // 如果mac地址不为空，转换成十六进制格式的字符串
                if (mac_b != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac_b.length; i++) {
                        sb.append(String.format("%02X%s", mac_b[i], (i < mac_b.length - 1) ? "-" : ""));
                    }
                    mac += sb + ",";
                }
            }
            mac = mac.substring(0, mac.length() - 1);
        }

        MyServerApi.code = code;
        MyServerApi.mac = mac;
        MyServerApi.phone = phone;
        HttpResponse httpResponse;
        try {
            Map<String, String> map = new HashMap<>(2);
            map.put("code", AsymmetricCryptoUtil.encryptWithPublicKey("21ebe0529bf74de"));
            map.put("mac", AsymmetricCryptoUtil.encryptWithPublicKey(mac));
            map.put("type", AsymmetricCryptoUtil.encryptWithPublicKey("selenium"));
            httpResponse = HttpUtils.doGet("/api/c/dy/open/encrypt/activateByCode", map);
        } catch (Exception e) {
            e.printStackTrace();
            httpResponse = null;
        }
        return HttpUtils.getBody(httpResponse);
    }


    /**
     * @author cjl
     * @description 验证卡密和mac地址
     * @dateTime 2024-02-19 13:02:53 (注释添加时间,包括但不完全代表创建或完成时间)

     * @return java.lang.String
     */
    public static String verificationCode() {
        HttpResponse httpResponse;
        try {
            Map<String, String> map = new HashMap<>(2);
            map.put("code", AsymmetricCryptoUtil.encryptWithPublicKey(code));
            map.put("mac", AsymmetricCryptoUtil.encryptWithPublicKey(mac));
            map.put("type", "gpt");
            httpResponse = HttpUtils.doGet("/api/c/dy/open/verifyCode", map);
        } catch (Exception e) {
            e.printStackTrace();
            httpResponse = null;
        }
        return HttpUtils.getBody(httpResponse);
    }
}
