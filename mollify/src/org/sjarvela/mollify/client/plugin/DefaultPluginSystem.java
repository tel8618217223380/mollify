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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.session.SessionInfo;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPluginSystem implements PluginSystem {
	private static Logger logger = Logger.getLogger(DefaultPluginSystem.class
			.getName());

	// private final List<Plugin> plugins = new ArrayList();
	// private final Map<String, Plugin> pluginsById = new HashMap();
	private final ClientInterface pluginEnv;

	// TODO move this entire class into external js
	@Inject
	public DefaultPluginSystem(ClientInterface pluginEnv) {
		this.pluginEnv = pluginEnv;
	}

	@Override
	public void setup(final SessionInfo session, final Callback onReady) {
		Map<String, String> externalPluginScripts = getExternalPluginScripts(session);
		if (externalPluginScripts.isEmpty()) {
			init(session, onReady);
		} else {
			new JQueryScriptLoader().load(externalPluginScripts,
					new Callback() {
						@Override
						public void onCallback() {
							init(session, onReady);
						}
					});
		}
	}

	private Map<String, String> getExternalPluginScripts(SessionInfo session) {
		logger.log(Level.INFO, "Initializing client plugins from session");
		JavaScriptObject pluginsObj = session.getPlugins();
		if (pluginsObj == null)
			return Collections.EMPTY_MAP;

		Map<String, String> result = new HashMap();
		JsObj plugins = pluginsObj.cast();
		for (String id : plugins.getKeys()) {
			if (id == null || id.length() == 0 || id.startsWith("_"))
				continue;
			logger.log(Level.INFO, "Initializing client plugin " + id);
			JsObj plugin = plugins.getJsObj(id).cast();

			if (plugin.hasValue("client_plugin"))
				result.put(id, plugin.getString("client_plugin"));
		}
		return result;
	}

	private void init(SessionInfo session, Callback onReady) {
		JavaScriptObject env = pluginEnv.getJsEnv(session.getPluginBaseUrl());
		initLib(env);
		// setupPlugins();
		// initializePlugins(env);
		// pluginEnv.onPluginsInitialized(plugins);
		onReady.onCallback();
	}

	private native void initLib(JavaScriptObject env) /*-{
		if (!$wnd.mollify)
			return;
		$wnd.mollify.setup(env);
	}-*/;

	// private void initializePlugins(JavaScriptObject env) {
	// logger.log(Level.INFO, "Initializing client plugins");
	// for (Plugin p : plugins)
	// p.initialize(env);
	// }

	// private native void setupPlugins() /*-{
	// if (!$wnd.mollify || !$wnd.mollify.getPlugins)
	// return;
	//
	// var plugins = $wnd.mollify.plugins();
	// if (!plugins || plugins.length == 0)
	// return;
	//
	// for (var i=0, j=plugins.length; i < j; i++) {
	// var plugin = plugins[i];
	// if (!plugin || !plugin.getPluginInfo || !plugin.getPluginInfo())
	// continue;
	// this.@org.sjarvela.mollify.client.plugin.DefaultPluginSystem::addPlugin(Lcom/google/gwt/core/client/JavaScriptObject;)(plugin);
	// }
	// }-*/;

	// public void addPlugin(JavaScriptObject p) {
	// if (p == null)
	// return;
	// Plugin plugin = p.cast();
	// PluginInfo info = plugin.getPluginInfo();
	// if (info == null) {
	// logger.log(Level.INFO, "Plugin ignored, does not provide info");
	// return;
	// }
	// plugins.add(plugin);
	//
	// String id = info.getId();
	// logger.log(Level.INFO, "Plugin registered: " + id);
	// pluginsById.put(id, plugin);
	// };
}
