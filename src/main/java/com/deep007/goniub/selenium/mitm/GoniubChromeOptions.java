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

@Slf4j
public class GoniubChromeOptions extends ChromeOptions {

	public static final String ANDROID_USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; PLUS Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.98 Mobile Safari/537.36";

	public static final String IOS_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0_1 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A402 Safari/604.1";

	public static final String CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0";

	private static String CHROME_PATH;
	private static String CHROME_DRIVER;

	public final String userAgent;

	public final boolean hideFingerprint;
	public final boolean disableLoadImage;
	public final boolean headless;
	public static void setPath(String driverPath){
		setPath(driverPath,null);
	}

	public static void setPath(String driverPath,String chromePath){
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
		this(disableLoadImage, headless, true, null, CHROME_USER_AGENT);
	}

	public GoniubChromeOptions(boolean disableLoadImage, boolean headless, boolean hideFingerprint, HttpsProxy httpsProxy,
							   String userAgent) {
		this.disableLoadImage = disableLoadImage;
		this.headless = headless;
		this.hideFingerprint = hideFingerprint;
		ChromeOptions options = this;
		if ((CHROME_DRIVER != null && new File(CHROME_DRIVER).exists()) || Boot.isMacSystem()) {
			String chromeDriver = CHROME_DRIVER;
			if (chromeDriver == null) {
				chromeDriver = System.getProperty("webdriver.chrome.driver");
			}
			if (chromeDriver == null) {
				throw new RuntimeException("请设置CHROME_DRIVER的路径");
			}
			System.setProperty("webdriver.chrome.driver", chromeDriver);
		}else if (Boot.isLinuxSystem()&&CHROME_PATH==null) {
			String CHROME_BINARY = LinuxTerminalHelper.findAbsoluteVar("google-chrome");
			if (CHROME_BINARY == null || "google-chrome".equals(CHROME_BINARY)) {
				throw new RuntimeException("请安装google-chrome.");
			}
			options.setBinary(CHROME_BINARY);
		}else if (Boot.isWindowsSystem()) {


		}
		if (Boot.isLinuxSystem() && headless) {
			options.addArguments("--headless");// headless mode
		}
		System.out.println("CHROME_PATH:"+CHROME_PATH);
		if (CHROME_PATH!=null){
			options.setBinary(CHROME_PATH);
		}
		options.addArguments("--header-args");
		options.addArguments("--disable-gpu");
		options.addArguments("--ignore-certificate-errors");
		options.addArguments("--no-sandbox"); //关闭沙盒模式
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-blink-features=AutomationControlled");
		//options.addArguments("user-data-dir=C:/Users/Administrator/AppData/Local/Google/Chrome/User Data");//待研究
		//不提示“Chrome正受到自动测试软件控制” 
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation") );
		//options.setExperimentalOption("useAutomationExtension", false);

		if (userAgent == null) {
			userAgent = CHROME_USER_AGENT;
		}
		this.userAgent = userAgent;
		options.addArguments("--user-agent=" + userAgent);
		//忽略ssl错误
		options.setCapability("acceptSslCerts", true);
		options.setCapability("acceptInsecureCerts", true);
		if (httpsProxy != null) {
			if (httpsProxy.getUsername() != null && httpsProxy.getPassword() != null) {
				options.addArguments("--start-maximized");
				File extension = ChromeExtensionUtil.createProxyauthExtension(httpsProxy.getServer(), httpsProxy.getPort(),
						httpsProxy.getUsername(), httpsProxy.getPassword());
				log.info("createProxyauthExtension:" + extension.getAbsolutePath());
				options.addExtensions(extension);
			} else {
				options.addArguments("--disable-extensions");
				options.addArguments("proxy-server=" + httpsProxy.getServer() + ":" + httpsProxy.getPort());
			}
		} else {
			options.addArguments("--disable-extensions");
		}
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("profile.password_manager_enabled", false);
		if (disableLoadImage) {
			prefs.put("profile.managed_default_content_settings.images", 2); // 禁止下载加载图片
		}
		options.setExperimentalOption("prefs", prefs);
	}

	public String getUserAgent() {
		return userAgent;
	}
}
