package com.rocket.util;


import com.rocket.chrome.mitm.RocketChromeDriver;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v114.network.Network;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author cjl
 * @description
 * @project FileUtil.class
 * @dateTime 2024-03-03 16:32:35
 * @version 1.0
 */
public class ChromesService {
    public static final String LOCAL_ABSOLUTE_PATH = new File("").getAbsolutePath();
    public static final String CHROMES_TEMP = LOCAL_ABSOLUTE_PATH + "\\Chrome\\temp\\more\\";
    private static final Map<String, RocketChromeDriver> instance = new HashMap<>(50);
    private static final Map<String, String> idAndSessionId = new HashMap<>(50);
    @Setter
    private static String chromePath = LOCAL_ABSOLUTE_PATH + "\\Chrome\\";

    public static RocketChromeDriver getChrome() {
        return getChrome(String.valueOf(SnowflakeIdGeneratorUtil.getNewId()));
    }

    public static RocketChromeDriver getChrome(String id) {
        return getChrome(id, false);
    }

    public static RocketChromeDriver getChrome(String id, boolean isHide) {
        Boolean isCreate = isCreate(id);
        if (isCreate == null) {
            return getChrome();
        }
        RocketChromeDriver chromeDriver = instance.get(id);
        if (isCreate) {
            if (isLive(chromeDriver)) {
                return chromeDriver;
            }
        }
        return createChromeDriver(id, isHide);
    }

    public static void close(RocketChromeDriver chromeDriver) {
        if (isLive(chromeDriver)) {
            chromeDriver.quit();
        }
    }

    public static void closeAndRemove(RocketChromeDriver chromeDriver) {
        close(chromeDriver);
        String idByChrome = getIdByChrome(chromeDriver);
        if (Boolean.TRUE.equals(isCreate(idByChrome))) {
            instance.remove(idByChrome);
        }
    }

    private static synchronized RocketChromeDriver createChromeDriver(String id, boolean isHide) {
        RocketChromeDriver chromeDriver;
        int index = Math.max(instance.size(), getMaxNumberByDirectoryName());
        chromeDriver = ChromeInitUtils.getHideMockerFeatureDriver(chromePath + "chromedriver.exe", chromePath + "chrome.exe", CHROMES_TEMP + index + "\\data", CHROMES_TEMP + index + "\\cache", isHide);
        DevTools devTools = chromeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        instance.put(id, chromeDriver);
        return chromeDriver;
    }

    public static Boolean isCreate(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return instance.containsKey(id);
    }

    public static String getIdByChrome(RocketChromeDriver driver) {
        return instance.entrySet().stream().filter(entry -> entry.getValue().equals(driver)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public static Boolean isLive(String id) {
        Boolean isCreate = isCreate(id);
        if (Boolean.TRUE.equals(isCreate)) {
            return isLive(instance.get(id));
        }
        return false;
    }

    public static Boolean isLive(RocketChromeDriver chromeDriver) {
        if (chromeDriver == null) {
            return null;
        }
        try {
            System.out.println("getCookieNamed:" + chromeDriver.manage().getCookieNamed(""));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void setWSSessionId(String id, String sessionId) {
        idAndSessionId.put(id, sessionId);
    }

    public static String getWSSessionId(String id) {
        return idAndSessionId.get(id);
    }

    public static String getIdBySessionId(String sessionId) {
        return idAndSessionId.entrySet().stream().filter(entry -> entry.getValue().equals(sessionId)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public static void removeWSSessionId(String id) {
        idAndSessionId.remove(id);
    }

    /**
     * 获取CHROMES_TEMP目录中所有目录中名字是最大数字的目录名字
     * @return 最大数字的目录名字，如果没有目录则返回null
     */
    public static int getMaxNumberByDirectoryName() {
        File tempDir = new File(CHROMES_TEMP);
        int maxNumber = -1;
        if (!tempDir.exists() || !tempDir.isDirectory()) {
            return maxNumber;
        }
        File[] subDirs = tempDir.listFiles(File::isDirectory);
        if (subDirs == null) {
            return maxNumber;
        }
        for (File subDir : subDirs) {
            try {
                int number = Integer.parseInt(subDir.getName());
                if (number > maxNumber) {
                    maxNumber = number;
                }
            } catch (NumberFormatException e) {
                // 忽略非数字目录名
            }
        }
        return maxNumber;
    }

}
