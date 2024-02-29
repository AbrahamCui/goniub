package com.deep007.goniub.hybridtest;

import com.deep007.goniub.selenium.mitm.GoniubChromeDriver;
import com.deep007.goniub.util.SeleniumUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

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

		GoniubChromeDriver hideMockerFeatureDriver = SeleniumUtils.getHideMockerFeatureDriver("F:\\work\\code\\java\\tiktok-assistant\\Chrome\\chromedriver.exe","F:\\work\\code\\java\\tiktok-assistant\\Chrome\\chrome.exe");
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
		System.setProperty("webdriver.chrome.driver", "/Users/stephen/Downloads/chromedriver");
		ChromeDriver driver = new ChromeDriver();
		driver.get("https://www.taobao.com");
		Object ret = driver.executeScript("return window.navigator.webdriver");
		System.out.println(ret);
		driver.quit();
	}
}
