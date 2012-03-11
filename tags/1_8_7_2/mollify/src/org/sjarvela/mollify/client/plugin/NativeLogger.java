/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeLogger {
	private Logger logger;

	public NativeLogger() {
		logger = Logger.getLogger("plugin");
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	public void logDebug(String s) {
		logger.log(Level.FINE, s);
	}

	public void logInfo(String s) {
		logger.log(Level.INFO, s);
	}

	public void logError(String s) {
		logger.log(Level.SEVERE, s);
	}

	private native JavaScriptObject createJs(NativeLogger l) /*-{
		var o = {};

		o.debug = function(s) {
			return l.@org.sjarvela.mollify.client.plugin.NativeLogger::logDebug(Ljava/lang/String;)(s);
		}
		o.info = function(s) {
			return l.@org.sjarvela.mollify.client.plugin.NativeLogger::logInfo(Ljava/lang/String;)(s);
		}
		o.error = function(s) {
			return l.@org.sjarvela.mollify.client.plugin.NativeLogger::logError(Ljava/lang/String;)(s);
		}
		return o;
	}-*/;
}
