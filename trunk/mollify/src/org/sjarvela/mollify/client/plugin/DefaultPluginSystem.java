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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPluginSystem implements PluginSystem {
	private static Logger logger = Logger.getLogger(DefaultPluginSystem.class
			.getName());
	private final List<Plugin> plugins = new ArrayList();
	private final Map<String, Plugin> pluginsById = new HashMap();
	private final PluginEnvironment pluginEnv;

	@Inject
	public DefaultPluginSystem(PluginEnvironment pluginEnv) {
		this.pluginEnv = pluginEnv;
	}

	@Override
	public void setup() {
		doSetup();
		initializePlugins();
	}

	private void initializePlugins() {
		JavaScriptObject env = pluginEnv.getJsEnv();
		for (Plugin p : plugins)
			p.initialize(env);
	}

	private native void doSetup() /*-{
		if (!$wnd.mollify || !$wnd.mollify.getPlugins) return;

		var plugins = $wnd.mollify.getPlugins();
		if (!plugins || plugins.length == 0) return;

		for(var i=0; i < plugins.length; i++) {
			var plugin = plugins[i];
			if (!plugin || !plugin.getPluginInfo || !plugin.getPluginInfo()) continue;
			this.@org.sjarvela.mollify.client.plugin.DefaultPluginSystem::addPlugin(Lcom/google/gwt/core/client/JavaScriptObject;)(plugin);
		}
	}-*/;

	public void addPlugin(JavaScriptObject p) {
		if (p == null)
			return;
		Plugin plugin = p.cast();
		PluginInfo info = plugin.getPluginInfo();
		if (info == null) {
			logger.log(Level.INFO, "Plugin ignored, does not provide info");
			return;
		}
		plugins.add(plugin);

		String id = info.getId();
		logger.log(Level.INFO, "Plugin registered: " + id);
		pluginsById.put(id, plugin);
	};
}
