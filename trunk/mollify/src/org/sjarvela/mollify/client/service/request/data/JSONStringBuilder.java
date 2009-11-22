/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request.data;

import com.google.gwt.core.client.JsArray;
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
		result.put(name, new JSONString(value));
		return this;
	}

	public JSONStringBuilder add(String name, int i) {
		result.put(name, new JSONNumber(i));
		return this;
	}

	public JSONStringBuilder add(String name, JsArray a) {
		result.put(name, new JSONObject(a));
		return this;
	}

	public JSONStringBuilder add(String name, double d) {
		result.put(name, new JSONNumber(d));
		return this;
	}

	@Override
	public String toString() {
		return result.toString();
	}

}
