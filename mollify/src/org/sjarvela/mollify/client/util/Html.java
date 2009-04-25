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
}
