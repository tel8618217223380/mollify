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

import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.request.UrlBuilder;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;

public class PhpService {
	private static final String SERVICE_FILE = "r.php";
	private static final String ADMIN_PATH = "admin/";
	private final String requestBaseUrl;
	protected final String adminUrl;
	private final UrlResolver urlResolver;
	private final int requestTimeout;

	enum RequestType {
		filesystem, session, configuration
	};

	// In development mode, MollifyService expects to get the full
	// url to the backend service in path parameter

	// For a production version, it is assumed that backend facade
	// (r.php) is in the same site.

	public PhpService(UrlResolver urlResolver, String path, int requestTimeout) {
		this.urlResolver = urlResolver;
		this.requestTimeout = requestTimeout;
		this.requestBaseUrl = getPath(path, SERVICE_FILE);
		this.adminUrl = getPath(path, ADMIN_PATH);
		Log.info("Mollify service location: " + this.requestBaseUrl
				+ ", timeout: " + requestTimeout + " sec");
	}

	private String getPath(String path, String p) {
		if (GWT.isScript())
			return urlResolver.getHostPageUrl(path, true) + p;

		if (path == null || path.length() == 0)
			throw new RuntimeException("Development service path not defined");
		return path + p;
	}

	public PhpRequestBuilder request() {
		return new PhpRequestBuilder().timeout(requestTimeout);
	}

	public UrlBuilder url() {
		return new UrlBuilder().baseUrl(requestBaseUrl);
	}
}