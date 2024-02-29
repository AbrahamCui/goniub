package com.deep007.goniub.util;

import com.deep007.goniub.selenium.mitm.GoniubChromeDriver;
import com.deep007.goniub.selenium.mitm.GoniubChromeOptions;

/**
 * @author cjl
 * @description 工具类
 * @project goniub
 * @dateTime 2024-02-29 21:49:34
 * @version 1.0
 */
public class SeleniumUtils {
    private static GoniubChromeDriver hideMockerFeatureDriver;
    public static void initChromeDriver(String driverPath) {
        initChromeDriver(driverPath,null);
    }
    /**
     * @author cjl
     * @description 设置 浏览器驱动和浏览器所在地址
     * @dateTime 2024-02-29 22:02:53 (注释添加时间,包括但不完全代表创建或完成时间)
     * @param driverPath java.lang.String
     * @param chromePath java.lang.String
     */
    public static void initChromeDriver(String driverPath,String chromePath) {
        //设置 浏览器驱动和浏览器所在地址
        GoniubChromeOptions.setPath(driverPath,chromePath)  ;
        hideMockerFeatureDriver = GoniubChromeDriver.newChromeInstance(false, false, null);
    }

    public static GoniubChromeDriver getHideMockerFeatureDriver() {
        return hideMockerFeatureDriver;
    }
    public static void clearDriver(){
        if (hideMockerFeatureDriver!=null){
            hideMockerFeatureDriver.quit();
            hideMockerFeatureDriver=null;
        }
    }
}
