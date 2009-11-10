/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client;

public class UrlResolver {
	private final String hostPageUrl;
	private final String moduleUrl;

	public UrlResolver(String hostPageUrl, String moduleUrl) {
		this.hostPageUrl = getBaseUrl(hostPageUrl);
		this.moduleUrl = getBaseUrl(moduleUrl);
	}

	private String getBaseUrl(String url) {
		if (url == null)
			return "";
		String baseUrl = url;
		if (baseUrl.length() > 0 && !baseUrl.endsWith("/"))
			baseUrl += "/";
		return baseUrl;
	}

	public String getHostPageUrl(String p, boolean folder) {
		return getUrl(hostPageUrl, p, folder);
	}

	public String getModuleUrl(String p, boolean folder) {
		return getUrl(moduleUrl, p, folder);
	}

	private String getUrl(String baseUrl, String p, boolean isFolder) {
		String base = (baseUrl == null) ? "" : baseUrl;
		String path = p == null ? "" : p.trim();

		if (path.toLowerCase().startsWith("http://")
				|| path.toLowerCase().startsWith("https://"))
			throw new RuntimeException("Illegal path definition: " + p);

		if (path.startsWith("/"))
			base = findRoot(baseUrl);

		String url = base + getOptionalPath(path);
		if (isFolder && url.length() > 0 && !url.endsWith("/"))
			url += "/";

		return url;
	}

	private String findRoot(String url) {
		int start = 0;
		String result;

		if (url.toLowerCase().startsWith("http://"))
			start = 7;
		if (url.toLowerCase().startsWith("https://"))
			start = 8;
		int pos = url.indexOf("/", start);

		if (pos < 0)
			result = url;
		else
			result = url.substring(0, pos);

		if (result.length() > 0 && !result.endsWith("/"))
			result += "/";

		return result;
	}

	private String getOptionalPath(String path) {
		if (path == null || path.length() == 0)
			return "";

		String result = path.trim();

		while (true) {
			if (result.isEmpty())
				break;
			char c = result.charAt(0);

			if (c == '.' || c == '/')
				result = result.substring(1);
			else
				break;
		}

		return result;
	}

}
