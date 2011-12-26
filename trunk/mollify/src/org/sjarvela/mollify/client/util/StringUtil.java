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

import java.util.List;

public class StringUtil {
	public static String toStringList(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s : list) {
			if (!first)
				sb.append(separator);
			sb.append(s);
			first = false;
		}
		return sb.toString();
	}
}
