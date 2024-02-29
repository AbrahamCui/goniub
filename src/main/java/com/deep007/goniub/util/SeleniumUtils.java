package com.deep007.goniub.util;

import com.deep007.goniub.selenium.mitm.GoniubChromeDriver;
import com.deep007.goniub.selenium.mitm.GoniubChromeOptions;

/**
 * 工具类
 * @author cjl
 * @version 1.0
 */
public class SeleniumUtils {
    private static GoniubChromeDriver hideMockerFeatureDriver;
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
        //设置 浏览器驱动和浏览器所在地址
        GoniubChromeOptions.setPath(driverPath,chromePath)  ;
        hideMockerFeatureDriver = GoniubChromeDriver.newChromeInstance(false, false, null);
    }
    public static GoniubChromeDriver getHideMockerFeatureDriver(String driverPath) {
        initChromeDriver(driverPath);
        return hideMockerFeatureDriver;
    }
    public static GoniubChromeDriver getHideMockerFeatureDriver(String driverPath,String chromePath) {
        initChromeDriver(driverPath,chromePath);
        return hideMockerFeatureDriver;
    }
    public static void clearDriver(){
        if (hideMockerFeatureDriver!=null){
            hideMockerFeatureDriver.quit();
            hideMockerFeatureDriver=null;
        }
    }
}
