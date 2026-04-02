package com.rocket.util;

import com.rocket.chrome.mitm.RocketChromeDriver;
import com.rocket.chrome.mitm.RocketChromeOptions;

/**
 * 工具类
 * @author cjl
 * @version 1.0
 */
public class ChromeInitUtils {
    private static RocketChromeDriver hideMockerFeatureDriver;
    private static void initChromeDriver(String driverPath) {
        initChromeDriver(driverPath,null);
    }
    /**
     * @author cjl
     * @description 设置 浏览器驱动和浏览器所在地址
     * @dateTime 2024-02-29 22:02:53 (注释添加时间,包括但不完全代表创建或完成时间)
     * @param driverPath java.lang.String
     * @param chromePath java.lang.String
     */
    private static void initChromeDriver(String driverPath,String chromePath) {
        initChromeDriver(driverPath,chromePath,false);
    }
    protected static RocketChromeDriver getHideMockerFeatureDriver(String driverPath) {
        initChromeDriver(driverPath);
        return hideMockerFeatureDriver;
    }
    protected static RocketChromeDriver getHideMockerFeatureDriver(String driverPath, String chromePath) {
        initChromeDriver(driverPath,chromePath);
        return hideMockerFeatureDriver;
    }
    protected static RocketChromeDriver getHideMockerFeatureDriver(String driverPath, String chromePath, String dataPath, String cachePath) {
//        initChromeDriver(driverPath,chromePath);
        RocketChromeOptions.setDATA(dataPath);
        RocketChromeOptions.setCACHE(cachePath);
        return getHideMockerFeatureDriver(driverPath,chromePath);
    }
    protected static RocketChromeDriver getHideMockerFeatureDriver(String driverPath, String chromePath, String dataPath, String cachePath, boolean isHide) {
//        initChromeDriver(driverPath,chromePath);
        RocketChromeOptions.setDATA(dataPath);
        RocketChromeOptions.setCACHE(cachePath);
        return getHideMockerFeatureDriver(driverPath,chromePath,isHide);
    }

    private static RocketChromeDriver getHideMockerFeatureDriver(String driverPath, String chromePath, boolean isHide) {
        initChromeDriver(driverPath,chromePath,isHide);
        return hideMockerFeatureDriver;
    }

    private static void initChromeDriver(String driverPath, String chromePath, boolean isHide) {
        //设置 浏览器驱动和浏览器所在地址
        RocketChromeOptions.setPath(driverPath,chromePath);
        try {
            hideMockerFeatureDriver = RocketChromeDriver.newChromeInstance(false, isHide, null);
        }catch (Exception e){
            hideMockerFeatureDriver=null;
            e.printStackTrace();
        }
    }
}
