/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.service.request.UrlParam.Encoding;
import org.sjarvela.mollify.client.util.Base64;
import org.sjarvela.mollify.client.util.Html;
import org.sjarvela.mollify.client.util.MD5;

import com.google.gwt.http.client.URL;

public class UrlBuilder {

	private final String requestBaseUrl;
	private List<UrlParam> params = new ArrayList();

	public UrlBuilder(String requestBaseUrl) {
		this.requestBaseUrl = requestBaseUrl;
	}

	public void add(UrlParam param) {
		params.add(param);
	}

	public void add(List<UrlParam> params) {
		this.params.addAll(params);
	}

	public String getUrl() {
		StringBuilder result = new StringBuilder(requestBaseUrl);
		boolean first = true;
		for (UrlParam param : params) {
			if (first)
				result.append('?');
			else
				result.append('&');
			addParam(result, param);
			first = false;
		}
		return result.toString();
	}

	private void addParam(StringBuilder result, UrlParam param) {
		String name = param.getName();
		String value = param.getValue();
		Encoding encoding = param.getEncoding();

		if (UrlParam.Encoding.URL.equals(encoding)) {
			value = URL.encodeComponent(value);
		} else if (UrlParam.Encoding.URL_FULL.equals(encoding)) {
			value = Html.fullUrlEncode(value);
		} else if (UrlParam.Encoding.BASE64.equals(encoding)) {
			value = Base64.encode(value);
		} else if (UrlParam.Encoding.MD5.equals(encoding)) {
			value = MD5.generate(value);
		}

		result.append(name).append('=').append(value);
	}
}
