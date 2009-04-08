/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.php;

import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.service.request.json.JsonRequestHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public class PhpService {
	private static final String SERVICE_FILE = "service.php";
	private final int requestTimeout;
	private final String requestBaseUrl;

	enum RequestType {
		file_data, file_action, session, configuration
	};

	// In hosted mode (development), MollifyService expects to get the full
	// url to the backend service in path parameter

	// For a standalone version, it is assumed that backend facade
	// (service.php) is in the same directory, or its descendants, than the
	// host page.

	public PhpService(String path, int requestTimeout) {
		this.requestTimeout = requestTimeout;
		this.requestBaseUrl = getBaseUrl(path);
		Log.info("Mollify service location: " + this.requestBaseUrl
				+ ", timeout: " + requestTimeout);
	}

	private String getBaseUrl(String path) {
		String result;

		if (GWT.isScript()) {
			result = GWT.getHostPageBaseURL() + getOptionalPath(path);
		} else {
			if (path == null || path.length() == 0)
				throw new RuntimeException(
						"Development service path not defined");
			result = path;
		}

		return result + SERVICE_FILE;
	}

	private String getOptionalPath(String path) {
		if (path == null || path.length() == 0)
			return "";

		String result = path.trim();

		if (path.toLowerCase().startsWith("http://"))
			result = result.substring(7);

		while (true) {
			char c = result.charAt(0);

			if (c == '.' || c == '/')
				result = result.substring(1);
			else
				break;
		}

		if (result.length() > 0 && !result.endsWith("/"))
			result += "/";

		return result;
	}

	String getUrl(RequestType type, String... params) {
		return getUrl(type, Arrays.asList(params));
	}

	String getUrl(RequestType type, List<String> params) {
		String url = requestBaseUrl + "?type=" + type.name();
		for (String param : params)
			url += "&" + param;
		return url;
	}

	void doRequest(String url, ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Requesting: " + url);
		new JsonRequestHandler(URL.encode(url), resultListener, requestTimeout)
				.doRequest();
	}
}