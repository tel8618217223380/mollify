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
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPluginSystem implements PluginSystem {
	private final List<Plugin> plugins = new ArrayList();
	private final PluginEnvironment pluginEnv;

	@Inject
	public DefaultPluginSystem(PluginEnvironment pluginEnv) {
		this.pluginEnv = pluginEnv;
	}

	@Override
	public void setup() {
		doSetup(this);
		initializePlugins();
	}

	private void initializePlugins() {
		JavaScriptObject env = pluginEnv.getJsEnv();
		for (Plugin p : plugins)
			p.initialize(env);
	}

	private native void doSetup(DefaultPluginSystem p) /*-{
		if (!$wnd.onMollifyStarted) return;

		$wnd.registerPlugin = function(plugin) {
			p.@org.sjarvela.mollify.client.plugin.DefaultPluginSystem::onRegisterPlugin(Lcom/google/gwt/core/client/JavaScriptObject;)(plugin);
		}
		$wnd.onMollifyStarted();
	}-*/;

	public void onRegisterPlugin(JavaScriptObject p) {
		if (p == null)
			return;
		Plugin plugin = p.cast();
		PluginInfo info = plugin.getPluginInfo();
		if (info == null) {
			Log.debug("Plugin ignored, does not provide info");
			return;
		}
		plugins.add(plugin);
	};
}
