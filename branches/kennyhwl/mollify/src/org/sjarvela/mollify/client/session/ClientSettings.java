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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;

public class ClientSettings {
	private static Logger logger = Logger.getLogger(ClientSettings.class
			.getName());
	private final ParameterParser parser;

	private static boolean getBool(String string) {
		if (string == null)
			throw new RuntimeException("Missing parameter " + string);

		String val = string.trim().toLowerCase();
		if (val.equals("yes") || val.equals("true") || val.equals("on"))
			return true;
		return false;
	}

	private static int getInt(String string) {
		if (string == null)
			throw new RuntimeException("Missing parameter " + string);

		return Integer.parseInt(string.trim());
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

	public int getInt(String param, int defaultValue) {
		if (!parser.hasParameter(param))
			return defaultValue;

		try {
			return getInt(parser.getParameter(param));
		} catch (NumberFormatException e) {
			if (LogConfiguration.loggingIsEnabled())
				logger.log(Level.INFO, "Invalid integer parameter " + param
						+ " value, using default " + defaultValue);
			return defaultValue;
		}
	}
}
