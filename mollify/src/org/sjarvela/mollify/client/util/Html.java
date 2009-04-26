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

public class Html {
	public static String convertLineBreaks(String s) {
		StringBuilder result = new StringBuilder();
		int length = s.length();

		boolean skip = false;
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			char n = (length > i) ? s.charAt(i + 1) : 0;

			if (!skip) {
				if (c == (char) 10) {
					result.append("<br/>");
				} else if (c == (char) 13) {
					result.append("<br/>");
					if (n == (char) 10)
						skip = true;
				} else {
					result.append(c);
				}
			}
			skip = false;
		}
		return result.toString();
	}

	static List<String> SAFE_TAGS = Arrays.asList(new String[] { "b", "br",
			"i", "a", "li", "ol", "ul", "span", "code", "p", "u" });

	public static List<String> findUnsafeTags(String html) {
		List<String> unsafe = new ArrayList();
		for (String tag : findTags(html)) {
			if (!SAFE_TAGS.contains(tag))
				unsafe.add(tag);
		}
		return unsafe;
	}

	private static List<String> findTags(String html) {
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
