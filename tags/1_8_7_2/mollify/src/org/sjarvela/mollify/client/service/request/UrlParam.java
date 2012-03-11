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

public class UrlParam {
	public enum Encoding {
		NONE, URL, URL_FULL, BASE64, MD5
	};

	private final String name;
	private final String value;
	private final Encoding encoding;

	public UrlParam(String name, String value) {
		this(name, value, Encoding.URL);
	}

	public UrlParam(String name, String value, Encoding encoding) {
		this.name = name;
		this.value = value;
		this.encoding = encoding;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public Encoding getEncoding() {
		return encoding;
	}
}
