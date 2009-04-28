/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.http.client.URL;

public class Html {
	static String ENCODED_CHARS = "&%?$#/\\\"@¨^'´`;€";

	public static String encodeSafeHtml(String text) {
		StringBuilder result = new StringBuilder();

		boolean inTag = false;
		for (char c : text.toCharArray()) {
			if (c == (char) 13 || c == (char) 10)
				continue;

			if (c == '<')
				inTag = true;
			else if (c == '>')
				inTag = false;

			if (!inTag && ENCODED_CHARS.indexOf(c) >= 0)
				result.append("&#" + Integer.toString(c) + ";");
			else
				result.append(c);
		}
		return result.toString();
	}

	static String URL_ENCODED_CHARS = "-_.!~*();/?:&=+$,#'\"";

	public static String fullUrlEncode(String s) {
		StringBuilder result = new StringBuilder();

		for (char c : URL.encode(s).toCharArray()) {
			int i = URL_ENCODED_CHARS.indexOf(c);
			if (i >= 0)
				result.append("%"
						+ Integer.toHexString(URL_ENCODED_CHARS.charAt(i)));
			else if (c != (char) 13 && c != (char) 10)
				result.append(c);
		}
		return result.toString();
	}

	static List<String> SAFE_HTML_TAGS = Arrays.asList(new String[] { "b",
			"br", "i", "a", "li", "ol", "ul", "span", "code", "p", "u" });

	public static List<String> findUnsafeHtmlTags(String html) {
		List<String> unsafe = new ArrayList();
		for (String tag : findHtmlTags(html)) {
			if (!SAFE_HTML_TAGS.contains(tag))
				unsafe.add(tag);
		}
		return unsafe;
	}

	private static List<String> findHtmlTags(String html) {
		List<String> result = new ArrayList();
		if (html == null || html.length() == 0)
			return result;

		int start = 0;
		while (true) {
			start = html.indexOf('<', start);
			if (start < 0)
				break;

			int end = html.indexOf('>', start);
			if (end < 0)
				break;

			String tag = html.substring(start + 1, end).trim().toLowerCase();
			if (!tag.startsWith("/")) {
				if (tag.endsWith("/"))
					tag = tag.substring(0, tag.length() - 1);
				result.add(tag.split(" ", 2)[0]);
			}

			start = end;
		}
		return result;
	}
}
