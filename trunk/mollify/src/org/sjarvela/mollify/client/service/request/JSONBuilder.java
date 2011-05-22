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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JSONBuilder {
	private final JSONObject result = new JSONObject();
	private final List<JSONArrayBuilder> arrays = new ArrayList();
	private final Map<String, JSONBuilder> objects = new HashMap();

	public JSONBuilder() {
	}

	public JSONBuilder(String name, String val) {
		add(name, val);
	}

	public JSONBuilder add(String name, String value) {
		result.put(name, value == null ? null : new JSONString(value));
		return this;
	}

	public JSONBuilder add(String name, int i) {
		result.put(name, new JSONNumber(i));
		return this;
	}

	public JSONBuilder add(String name, JsArray a) {
		result.put(name, a == null ? null : new JSONObject(a));
		return this;
	}

	public JSONBuilder add(String name, double d) {
		result.put(name, new JSONNumber(d));
		return this;
	}

	public JSONBuilder object(String name, JavaScriptObject jso) {
		result.put(name, new JSONObject(jso));
		return this;
	}

	public JSONBuilder object(String name) {
		JSONBuilder builder = new JSONBuilder();
		objects.put(name, builder);
		return builder;
	}

	public JSONArrayBuilder array(String name) {
		JSONArray a = new JSONArray();
		result.put(name, a);
		JSONArrayBuilder arrayBuilder = new JSONArrayBuilder(this, a);
		arrays.add(arrayBuilder);
		return arrayBuilder;
	}

	public JavaScriptObject toJSON() {
		for (Entry<String, JSONBuilder> e : objects.entrySet())
			result.put(e.getKey(), new JSONObject(e.getValue().toJSON()));

		for (JSONArrayBuilder arrayBuilder : arrays)
			arrayBuilder.buildObjects();
		return result.getJavaScriptObject();
	}

	@Override
	public String toString() {
		return new JSONObject(toJSON()).toString();
	}

	public static class JSONArrayBuilder {
		private final JSONBuilder parent;
		private final JSONArray array;
		private final List<JSONBuilder> objects = new ArrayList();

		public JSONArrayBuilder(JSONBuilder parent, JSONArray array) {
			this.parent = parent;
			this.array = array;
		}

		public void buildObjects() {
			for (JSONBuilder o : objects)
				array.set(array.size(), new JSONObject(o.toJSON()));
		}

		public JSONBuilder parent() {
			return parent;
		}

		public JSONArrayBuilder add(String item) {
			array.set(array.size(), new JSONString(item));
			return this;
		}

		public JSONBuilder addObject() {
			JSONBuilder builder = new JSONBuilder();
			objects.add(builder);
			return builder;
		}
	}

}
