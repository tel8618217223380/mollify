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
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

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
	
	public static JsArray asJsArray(JavaScriptObject... list) {
		JsArray a = JsArray.createArray().cast();
		int index = 0;
		for (JavaScriptObject o : list)
			a.set(index++, o);
		return a;
	}

	public static String asJsonString(Map<String, JavaScriptObject> data) {
		return asJsonString(data, new ArrayList(data.keySet()));
	}

	public static String asJsonString(JavaScriptObject obj) {
		return new JSONObject(obj).toString();
	}

	public static String asJsonString(Map<String, JavaScriptObject> data,
			List<String> order) {
		JSONObject o = new JSONObject();
		for (String key : order)
			o.put(key, new JSONObject(data.get(key)));
		return o.toString();
	}

	public static JavaScriptObject asJavascriptObject(Map<String, String> data) {
		JSONObject o = new JSONObject();
		for (Entry<String, String> e : data.entrySet())
			o.put(e.getKey(), new JSONString(e.getValue()));
		return o.getJavaScriptObject();
	}

	public static List<String> asList(JsArrayString array) {
		List<String> list = new ArrayList();
		int s = array.length();
		for (int i = 0; i < s; i++)
			list.add(array.get(i));
		return list;
	}

	public static JsArrayString asArray(List<String> list) {
		JsArrayString array = JsArrayString.createArray().cast();
		int index = 0;
		for (String s : list)
			array.set(index++, s);
		return array;
	}

}
