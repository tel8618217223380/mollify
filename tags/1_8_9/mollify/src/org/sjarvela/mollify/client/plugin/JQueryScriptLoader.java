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

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.Callback;

public class JQueryScriptLoader {
	private static Logger logger = Logger.getLogger(JQueryScriptLoader.class
			.getName());

	private Map<String, String> scripts;
	private Callback onFinished;

	public JQueryScriptLoader() {
	}

	public void load(Map<String, String> scripts, Callback onFinished) {
		this.scripts = scripts;
		this.onFinished = onFinished;
		processNext();
	}

	private void processNext() {
		if (scripts.isEmpty()) {
			onFinished.onCallback();
			return;
		}
		Entry<String, String> script = scripts.entrySet().iterator().next();
		loadScript(script.getKey(), script.getValue());
	}

	private void loadScript(String id, String path) {
		logger.log(Level.INFO, "Loading script: " + id + " " + path);
		doLoadScript(this, id, path);
	}

	private native void doLoadScript(JQueryScriptLoader sl, String id,
			String path) /*-{
		$wnd.$.getScript(path, function() {
			sl.@org.sjarvela.mollify.client.plugin.JQueryScriptLoader::onScriptLoaded(Ljava/lang/String;)(id);
		});
	}-*/;

	public void onScriptLoaded(String id) {
		logger.log(Level.INFO, "Script loaded: " + id);
		scripts.remove(id);
		processNext();
	}
}
