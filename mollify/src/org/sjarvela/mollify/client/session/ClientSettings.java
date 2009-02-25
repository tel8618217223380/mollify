/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session;

public class ClientSettings {
	private final ParameterParser parser;

	private static boolean getBool(String string) {
		if (string == null)
			throw new RuntimeException("Missing parameter " + string);

		String val = string.trim().toLowerCase();
		if (val.equals("yes") || val.equals("true") || val.equals("on"))
			return true;
		return false;
	}

	public ClientSettings(ParameterParser parser) {
		this.parser = parser;
	}

	public String getString(String param) {
		return parser.getParameter(param);
	}

	public boolean getBool(String param, boolean defaultValue) {
		if (!parser.hasParameter(param))
			return defaultValue;
		return getBool(parser.getParameter(param));
	}
}
