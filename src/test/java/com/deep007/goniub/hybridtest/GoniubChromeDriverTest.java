package com.deep007.goniub.hybridtest;

import com.deep007.goniub.selenium.mitm.GoniubChromeDriver;
import com.deep007.goniub.selenium.mitm.GoniubChromeOptions;
import com.deep007.goniub.util.GoniubInitUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class GoniubChromeDriverTest {

	/**
	 * addScriptToEvaluateOnNewDocument 隐藏selenium特征测试
	 */
	@Test
	public void main(){
		//浏览器驱动
//		GoniubChromeOptions.CHROME_DRIVER = "F:\\work\\code\\java\\tiktok-assistant\\Chrome\\chromedriver.exe";
//		浏览器地址
//		GoniubChromeOptions.CHROME_PATH = "F:\\work\\code\\java\\tiktok-assistant\\Chrome\\chrome.exe";

		GoniubChromeDriver hideMockerFeatureDriver = GoniubInitUtils.getHideMockerFeatureDriver("F:\\work\\code\\java\\tiktok-assistant\\Chrome\\chromedriver.exe","F:\\work\\code\\java\\tiktok-assistant\\Chrome\\chrome.exe");
//		hideMockerFeatureDriver.get("https://www.taobao.com");
		hideMockerFeatureDriver.get("https://www.tiktok.com");
		Object ret = hideMockerFeatureDriver.executeScript("return window.navigator.webdriver");
		System.out.println(ret);
//		hideMockerFeatureDriver.quit();
	}

	/**
	 * 原生selenium测试（不隐藏）
	 * @throws Exception
	 */
	@Test
	public void main2() throws Exception {
		System.setProperty("webdriver.chrome.driver", "F:\\work\\code\\java\\tiktok-assistant\\Chrome\\chromedriver.exe");
		GoniubChromeOptions goniubChromeOptions = new GoniubChromeOptions(false, false,
				null, GoniubChromeOptions.CHROME_USER_AGENT);
		ChromeOptions options=new ChromeOptions();
//		options.addArguments("--header-args");
//		options.addArguments("--disable-gpu");
//		options.addArguments("--ignore-certificate-errors");
//		options.addArguments("--no-sandbox"); //关闭沙盒模式
//		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.setBinary("F:\\work\\code\\java\\tiktok-assistant\\Chrome\\chrome.exe");
		ChromeDriver driver = new ChromeDriver(options);
		driver.get("https://www.taobao.com");
		Object ret = driver.executeScript("return window.navigator.webdriver");
		System.out.println(ret);
//		driver.quit();
	}
}
