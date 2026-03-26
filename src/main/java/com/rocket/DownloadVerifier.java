package com.rocket;

import com.rocket.response.Page;

public interface DownloadVerifier {
	boolean verify(Page page) throws Exception;
}
