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
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;

public class JsUtil {
	public static <T> List<T> asList(JsArray array, Class<T> t) {
		List<T> result = new ArrayList();
		for (int index = 0; index < array.length(); index++)
			result.add((T) array.get(index));
		return result;
	}

	public static <T extends JavaScriptObject> JsArray asJsArray(Class<T> t,
			T... list) {
		return asJsArray(Arrays.asList(list), t);
	}

	public static <T extends JavaScriptObject> JsArray<T> asJsArray(
			List<T> list, Class<T> t) {
		JsArray<T> result = JsArray.createArray().cast();
		int index = 0;
		for (T o : list)
			result.set(index++, o);
		return result;
	}

	public static String asJsonString(Map<String, JavaScriptObject> data) {
		JSONObject o = new JSONObject();
		for (Entry<String, JavaScriptObject> e : data.entrySet())
			o.put(e.getKey(), new JSONObject(e.getValue()));
		return o.toString();
	}

	public static String asJsonString(JavaScriptObject obj) {
		return new JSONObject(obj).toString();
	}
}
