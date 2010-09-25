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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.environment.php.ActionId;

import com.google.gwt.http.client.URL;

public class UrlBuilder {
	private String baseUrl;
	private List<String> items = new ArrayList();

	public UrlBuilder baseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	public UrlBuilder fileItem(FileSystemItem item) {
		items.add(convertItemId(item));
		return this;
	}

	public UrlBuilder action(ActionId action) {
		items.add(action.name());
		return this;
	}

	public UrlBuilder item(String item) {
		if (item != null)
			items.add(item);
		return this;
	}

	// private void addParam(StringBuilder result, UrlParam param) {
	// String name = param.getName();
	// String value = param.getValue();
	// Encoding encoding = param.getEncoding();
	//
	// if (UrlParam.Encoding.URL.equals(encoding)) {
	// value = URL.encodeComponent(value);
	// } else if (UrlParam.Encoding.URL_FULL.equals(encoding)) {
	// value = Html.fullUrlEncode(value);
	// } else if (UrlParam.Encoding.BASE64.equals(encoding)) {
	// value = Base64.encode(value);
	// } else if (UrlParam.Encoding.MD5.equals(encoding)) {
	// value = MD5.generate(value);
	// }
	//
	// result.append(name).append('=').append(value);
	// }

	public String build() {
		StringBuilder result = new StringBuilder(baseUrl).append("/");
		for (String item : items)
			result.append(URL.encodeComponent(item)).append("/");

		// boolean first = true;
		// for (UrlParam param : params) {
		// if (first)
		// result.append('?');
		// else
		// result.append('&');
		// addParam(result, param);
		// first = false;
		// }
		return result.toString();
	}

	private String convertItemId(FileSystemItem item) {
		return item.getId().replaceAll("=", ",").replaceAll("\\+", "-")
				.replaceAll("/", "_");
	}
}
