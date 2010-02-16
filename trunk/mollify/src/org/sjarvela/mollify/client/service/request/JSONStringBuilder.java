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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JSONStringBuilder {
	private final JSONObject result = new JSONObject();

	public JSONStringBuilder() {
	}

	public JSONStringBuilder(String name, String val) {
		add(name, val);
	}

	public JSONStringBuilder add(String name, String value) {
		result.put(name, value == null ? null : new JSONString(value));
		return this;
	}

	public JSONStringBuilder add(String name, int i) {
		result.put(name, new JSONNumber(i));
		return this;
	}

	public JSONStringBuilder add(String name, JsArray a) {
		result.put(name, a == null ? null : new JSONObject(a));
		return this;
	}

	public JSONStringBuilder add(String name, double d) {
		result.put(name, new JSONNumber(d));
		return this;
	}

	public JSONArrayBuilder addArray(String name) {
		JSONArray a = new JSONArray();
		result.put(name, a);
		return new JSONArrayBuilder(this, a);
	}

	@Override
	public String toString() {
		return result.toString();
	}

	public static class JSONArrayBuilder {
		private final JSONStringBuilder parent;
		private final JSONArray array;

		public JSONArrayBuilder(JSONStringBuilder parent, JSONArray array) {
			this.parent = parent;
			this.array = array;
		}

		public JSONStringBuilder parent() {
			return parent;
		}

		public JSONArrayBuilder add(String item) {
			array.set(array.size(), new JSONString(item));
			return this;
		}

	}
}
