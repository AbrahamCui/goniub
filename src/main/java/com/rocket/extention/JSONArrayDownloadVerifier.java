package com.rocket.extention;

import com.alibaba.fastjson.JSON;
import com.rocket.DownloadVerifier;
import com.rocket.response.Page;

public class JSONArrayDownloadVerifier implements DownloadVerifier {

	@Override
	public boolean verify(Page page) throws Exception {
		return JSON.parseArray(page.getContent()) != null;
	}

}
