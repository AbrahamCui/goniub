package com.rocket.extention;

import com.alibaba.fastjson.JSON;
import com.rocket.DownloadVerifier;
import com.rocket.response.Page;

public class JSONObjectDownloadVerifier implements DownloadVerifier {

	@Override
	public boolean verify(Page page) throws Exception {
		return JSON.parseObject(page.getContent()) != null;
	}

}
