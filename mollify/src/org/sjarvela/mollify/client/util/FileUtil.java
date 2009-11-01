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

public class FileUtil {

	public static String getExtension(String filename) {
		if (filename == null || filename.length() == 0)
			return "";
		int lastPoint = filename.lastIndexOf('.');
		if (lastPoint < 0)
			return "";
		return filename.substring(lastPoint + 1);
	}

}
