/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client;

import java.util.HashMap;
import java.util.Map;

public class ParameterParser {
	private final String name;
	private final Map<String, String> values;

	public ParameterParser(String name) {
		this.name = name;
		this.values = readValues();
	}

	private Map<String, String> readValues() {
		Map<String, String> result = new HashMap();
		String[] values = getMetaParameter(name).split(";");

		for (String valueString : values) {
			if (valueString.length() == 0)
				continue;
			
			String[] parts = valueString.split("=");
			if (parts.length != 2)
				throw new RuntimeException("Invalid parameter: " + valueString);

			String name = parts[0].trim();
			String value = parts[1].trim();
			if (name.length() == 0 || value.length() == 0)
				throw new RuntimeException("Invalid parameter: " + valueString);

			result.put(name, value);
		}
		return result;
	}

	public String getParameter(String name) {
		if (!values.containsKey(name))
			return null;
		return values.get(name);
	}

	private native static String getMetaParameter(String name) /*-{
		var metaArray = $doc.getElementsByTagName("meta");
		var result = '';
		
		for (var i = 0; i < metaArray.length; i++) {
			if (metaArray[i].getAttribute("name") == name)
				result = result + metaArray[i].getAttribute("content") + ';';
		}
		return result;
	}-*/;
}
