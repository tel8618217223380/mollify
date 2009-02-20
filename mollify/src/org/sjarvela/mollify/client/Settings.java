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

public class Settings {

	private static final String PARAM_DEBUG = "debug";

	private final boolean debug;

	public static Settings create(ParameterParser parser) {
		String debugString = parser.getParameter(PARAM_DEBUG);
		return new Settings(getBool(debugString));
	}

	private static boolean getBool(String string) {
		if (string == null)
			return false;
		String val = string.trim().toLowerCase();
		if (val.equals("yes") || val.equals("true") || val.equals("on"))
			return true;
		return false;
	}

	public Settings(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}

}
