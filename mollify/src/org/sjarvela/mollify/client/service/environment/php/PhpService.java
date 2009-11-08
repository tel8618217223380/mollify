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

import org.sjarvela.mollify.client.UrlResolver;
import org.sjarvela.mollify.client.service.request.HtmlRequestHandlerFactory;
import org.sjarvela.mollify.client.service.request.UrlBuilder;
import org.sjarvela.mollify.client.service.request.UrlParam;
import org.sjarvela.mollify.client.service.request.listener.JsonRequestListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;

public class PhpService {
	private static final String SERVICE_FILE = "service.php";
	private final String requestBaseUrl;
	private final HtmlRequestHandlerFactory htmlRequestHandlerFactory;
	private final UrlResolver urlResolver;

	enum RequestType {
		filesystem, session, configuration
	};

	// In hosted mode (development), MollifyService expects to get the full
	// url to the backend service in path parameter

	// For a standalone version, it is assumed that backend facade
	// (service.php) is in the same directory, or its descendants, than the
	// host page.

	public PhpService(UrlResolver urlResolver, String path, int requestTimeout) {
		this.urlResolver = urlResolver;
		this.requestBaseUrl = getPath(path);
		this.htmlRequestHandlerFactory = new HtmlRequestHandlerFactory(
				requestTimeout);
		Log.info("Mollify service location: " + this.requestBaseUrl
				+ ", timeout: " + requestTimeout + " sec");
	}

	private String getPath(String path) {
		if (GWT.isScript())
			return urlResolver.getHostPageUrl(path) + SERVICE_FILE;

		if (path == null || path.length() == 0)
			throw new RuntimeException("Development service path not defined");
		return path + SERVICE_FILE;
	}

	String getUrl(RequestType type, UrlParam... params) {
		return getUrl(type, Arrays.asList(params));
	}

	String getUrl(RequestType type, List<UrlParam> params) {
		UrlBuilder result = new UrlBuilder(requestBaseUrl);
		result.add(new UrlParam("type", type.name()));
		result.add(params);
		return result.getUrl();
	}

	void doGetRequest(String url, final ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Request GET: " + url);

		htmlRequestHandlerFactory.createGET(url,
				new JsonRequestListener(resultListener)).doRequest();
	}

	void doPostRequest(String url, String data,
			final ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Request POST: " + url);

		JsonRequestListener listener = new JsonRequestListener(resultListener);
		htmlRequestHandlerFactory.createPOST(url, listener).withData(data)
				.doRequest();
	}
}