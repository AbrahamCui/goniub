package com.deep007.goniub.hybridtest;

import org.junit.jupiter.api.Test;

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

        /*try {
            GoniubChromeDriver hideMockerFeatureDriver = GoniubInitUtils.getHideMockerFeatureDriver("D:\\work\\code\\java\\tiktok-assistant\\Chrome\\chromedriver.exe","D:\\work\\code\\java\\tiktok-assistant\\Chrome\\chrome.exe");
//		hideMockerFeatureDriver.get("https://www.taobao.com");
            hideMockerFeatureDriver.get("https://www.baidu.com");
            System.out.println(33333);
//		hideMockerFeatureDriver.getDevTools().clearListeners();
            Object ret = hideMockerFeatureDriver.executeScript("return window.navigator.webdriver");
            System.out.println("ret:"+ret);
        }catch (Exception e){

        }*/

//		hideMockerFeatureDriver.quit();
	}

	/**
	 * 原生selenium测试（不隐藏）
	 * @throws Exception
	 */
	@Test
	public void main2() throws Exception {

//		driver.quit();
	}
}
