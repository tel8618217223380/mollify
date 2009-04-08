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
import java.util.List;

import com.google.gwt.core.client.JsArray;

public class JsUtil {
	public static <T> List<T> asList(JsArray array, Class<T> t) {
		List<T> result = new ArrayList();
		for (int index = 0; index < array.length(); index++)
			result.add((T) array.get(index));
		return result;
	}
}
