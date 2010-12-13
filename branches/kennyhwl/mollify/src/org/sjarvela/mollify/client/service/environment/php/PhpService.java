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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.request.ResponseProcessor;
import org.sjarvela.mollify.client.service.request.UrlBuilder;

import com.google.gwt.core.client.GWT;

public class PhpService {
	private static Logger logger = Logger.getLogger(PhpService.class.getName());

	private static final String SERVICE_FILE = "r.php";
	private static final String ADMIN_PATH = "admin/";
	private final String requestBaseUrl;
	private final String rootUrl;
	protected final String adminUrl;
	private final UrlResolver urlResolver;
	private final int requestTimeout;
	private final boolean limitedHttpMethods;
	protected final ResponseProcessor responseProcessor;

	enum RequestType {
		filesystem, session, configuration
	};

	// In development mode, MollifyService expects to get the full
	// url to the backend service in path parameter

	// For a production version, it is assumed that backend facade
	// (r.php) is in the same site.

	public PhpService(UrlResolver urlResolver, String path, int requestTimeout,
			boolean limitedHttpMethods, ResponseProcessor responseProcessor) {
		this.urlResolver = urlResolver;
		this.requestTimeout = requestTimeout;
		this.limitedHttpMethods = limitedHttpMethods;
		this.responseProcessor = responseProcessor;
		this.requestBaseUrl = getPath(path, SERVICE_FILE);
		this.rootUrl = path;
		this.adminUrl = getPath(path, ADMIN_PATH);

		logger.log(Level.INFO, "Mollify service location: "
				+ this.requestBaseUrl + ", timeout: " + requestTimeout + " sec");
	}

	private String getPath(String path, String p) {
		if (GWT.isScript())
			return urlResolver.getHostPageUrl(path, true) + p;

		if (path == null || path.length() == 0)
			throw new RuntimeException("Development service path not defined");
		if (!path.startsWith("http"))
			return urlResolver.getHostPageUrl(path, true) + p;
		return path + p;
	}

	public ResponseProcessor getResponseProcessor() {
		return responseProcessor;
	}

	public PhpRequestBuilder request() {
		return new PhpRequestBuilder(limitedHttpMethods, responseProcessor)
				.timeout(requestTimeout);
	}

	public UrlBuilder serviceUrl() {
		return new UrlBuilder().baseUrl(requestBaseUrl);
	}

	public UrlBuilder pluginUrl(String name) {
		return new UrlBuilder().baseUrl(rootUrl).item("plugin").item(name);
	}

}