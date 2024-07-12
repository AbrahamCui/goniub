package com.deep007.goniub.selenium.mitm;

import com.deep007.goniub.request.HttpsProxy;
import com.deep007.goniub.terminal.LinuxTerminalHelper;
import com.deep007.goniub.util.Boot;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class GoniubChromeOptions extends ChromeOptions {

    public static final String ANDROID_USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; PLUS Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.98 Mobile Safari/537.36";

    public static final String IOS_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0_1 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A402 Safari/604.1";

    public static final String CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0";

    private static String CHROME_PATH;
    private static String CHROME_DRIVER;
    private static String DATA;
    private static String CACHE;
    private static String tmpdir = System.getProperty("java.io.tmpdir");
    private static String dir = tmpdir + File.separator + "chrome_file_data_cache" + File.separator + 1;
    public final String userAgent;

    public final boolean hideFingerprint;
    public final boolean disableLoadImage;
    public final boolean headless;

    public static void setPath(String driverPath) {
        setPath(driverPath, null);
    }

	public static void setDATA(String DATA) {
		GoniubChromeOptions.DATA = DATA;
	}

	public static void setCACHE(String CACHE) {
		GoniubChromeOptions.CACHE = CACHE;
	}

	public static void setPath(String driverPath, String chromePath) {
        //浏览器驱动
        CHROME_DRIVER = driverPath;
        //浏览器地址
        CHROME_PATH = chromePath;
    }


    public GoniubChromeOptions() {
        this(false, false);
    }

    public GoniubChromeOptions(boolean disableLoadImage, boolean headless) {
        this(disableLoadImage, headless, true, null, CHROME_USER_AGENT);
    }

    public GoniubChromeOptions(boolean disableLoadImage, boolean headless, HttpsProxy httpsProxy,
                               String userAgent) {
        this(disableLoadImage, headless, true, httpsProxy, CHROME_USER_AGENT);
    }

    private static AtomicInteger fileSerial = new AtomicInteger(0);

    public GoniubChromeOptions(boolean disableLoadImage, boolean headless, boolean hideFingerprint, HttpsProxy httpsProxy,
                               String userAgent) {
        System.out.println("tmpdir:" + tmpdir);
        this.disableLoadImage = disableLoadImage;
        this.headless = headless;
        this.hideFingerprint = hideFingerprint;
//		ChromeOptions options = this;
        if ((CHROME_DRIVER != null && new File(CHROME_DRIVER).exists()) || Boot.isMacSystem()) {
            String chromeDriver = CHROME_DRIVER;
            if (chromeDriver == null) {
                chromeDriver = System.getProperty("webdriver.chrome.driver");
            }
            if (chromeDriver == null) {
                throw new RuntimeException("请设置CHROME_DRIVER的路径");
            }
            System.setProperty("webdriver.chrome.driver", chromeDriver);
        } else if (Boot.isLinuxSystem() && CHROME_PATH == null) {
            String CHROME_BINARY = LinuxTerminalHelper.findAbsoluteVar("google-chrome");
            if (CHROME_BINARY == null || "google-chrome".equals(CHROME_BINARY)) {
                throw new RuntimeException("请安装google-chrome.");
            }
            this.setBinary(CHROME_BINARY);
        } else if (Boot.isWindowsSystem()) {
        }
        if (headless) {
            this.addArguments("--headless");// headless mode
        }
        System.out.println("CHROME_PATH:" + CHROME_PATH);
        if (CHROME_PATH != null) {
            this.setBinary(CHROME_PATH);
        }
        this.addArguments("--remote-allow-origins=*");

        System.out.println(12123123);
        this.addArguments("--remote-debugging-port=13628");
//        this.addArguments("--debuggerAddress=127.0.0.1:8081");
        this.addArguments("--allowed-origins=*");
        this.addArguments("--allowed-ips=*");
        //修改ip白名单--待验证
//        this.addArguments("--whitelistedIps=''");
        this.addArguments("--header-args");
        this.addArguments("--disable-gpu");
        this.addArguments("--ignore-certificate-errors");
//		this.addArguments("--no-sandbox"); //关闭沙盒模式
//		this.addArguments("--disable-dev-shm-usage");
//		this.addArguments("--auto-open-devtools-for-tabs");
        this.addArguments("--disable-blink-features=AutomationControlled"); //window.navigator.webdriver=false
        //this.addArguments("user-data-dir=C:/Users/Administrator/AppData/Local/Google/Chrome/User Data");//待研究
        //不提示“Chrome正受到自动测试软件控制”
        this.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
//        this.setExperimentalOption("debuggerAddress", "127.0.0.1:13888");
        //this.setExperimentalOption("useAutomationExtension", false);
//		System.setProperty("webdriver.chrome.whitelistedIps", "");

        if (DATA == null) {
            DATA = dir + File.separator + "data";
        }
        File file1 = new File(DATA);
        if (file1.exists()) {
            file1.mkdirs();
        }
        this.addArguments("--user-data-dir=" + file1.getAbsolutePath()); //解决打开页面出现data;空白页面情况,因为没有缓存目录
        if (CACHE == null) {
            CACHE = dir + File.separator + "cache";
        }
        File file2 = new File(CACHE);
        if (file2.exists()) {
            file1.mkdirs();
        }

        this.addArguments("--disk-cache-dir=" + file2.getAbsolutePath()); //指定Cache路径
        if (userAgent == null) {
            userAgent = CHROME_USER_AGENT;
        }
        this.userAgent = userAgent;
        this.addArguments("--user-agent=" + userAgent);
        //忽略ssl错误
//		this.setCapability("acceptSslCerts", true);
        this.setCapability("acceptInsecureCerts", true);
        if (httpsProxy != null) {
            if (httpsProxy.getUsername() != null && httpsProxy.getPassword() != null) {
                this.addArguments("--start-maximized");
                File extension = ChromeExtensionUtil.createProxyauthExtension(httpsProxy.getServer(), httpsProxy.getPort(),
                        httpsProxy.getUsername(), httpsProxy.getPassword());
                log.info("createProxyauthExtension:" + extension.getAbsolutePath());
                this.addExtensions(extension);
            } else {
                this.addArguments("--disable-extensions");
                this.addArguments("proxy-server=" + httpsProxy.getServer() + ":" + httpsProxy.getPort());
            }
        } else {
//			this.addArguments("--disable-extensions");
        }
        Map<String, Object> prefs = new HashMap<>();
        prefs.put(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.password_manager_enabled", false);
        //		prefs.put("profile.default_content_setting_values.notifications", 2);
        //		prefs.put("profile.default_content_setting_values.images", 2);
        if (disableLoadImage) {
            prefs.put("profile.managed_default_content_settings.images", 2); // 禁止下载加载图片
            //"profile.default_content_setting_values.notifications": 2,
            //    "profile.default_content_setting_values.images": 2
        }
        this.setExperimentalOption("prefs", prefs);


		/*this.addFiddler(new FiddlerOption
				　　{
    OnBeforeRequestOptions = new List<FiddlerOnBeforeRequestOptions>
    {
        // 配置转发
        new FiddlerOnBeforeRequestOptions
        {
            Match = "https://www.cnblogs.com/yudongdong/ajax/GetPostStat",//正则
            RedirectUrl = "http://localhost:5000/GetPostStat",//如果匹配成功则将requestBody转发到这个url中去
            Cancel = false//如果配置了cancel=true那么转发将无效，true的意思是直接拦截这次的请求,不去发送了
        },
        // 配置拦截
        new FiddlerOnBeforeRequestOptions
        {
            Match = "https://www.cnblogs.com/yudongdong/ajax/blogStats",
            Cancel = true//true的意思是直接拦截这次的请求,不去发送了
        },
    }
});*/


    }

    public String getUserAgent() {
        return userAgent;
    }
}
