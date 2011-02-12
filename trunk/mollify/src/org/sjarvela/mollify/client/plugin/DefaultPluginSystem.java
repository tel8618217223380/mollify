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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.FileView;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.session.SessionInfo;

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
	public void setup(final FileView fileView, final SessionInfo session) {
		Map<String, String> externalPluginScripts = getExternalPluginScripts(session);
		if (externalPluginScripts.isEmpty()) {
			init(fileView, session);
		} else {
			new JQueryScriptLoader().load(externalPluginScripts,
					new Callback() {
						@Override
						public void onCallback() {
							init(fileView, session);
						}
					});
		}
	}

	private Map<String, String> getExternalPluginScripts(SessionInfo session) {
		logger.log(Level.INFO, "Initializing client plugins from session");
		JavaScriptObject pluginsObj = session.getPlugins();
		if (plugins == null)
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

	private void init(FileView fileView, SessionInfo session) {
		setupPlugins();
		initializePlugins(fileView, session);
	}

	private void initializePlugins(FileView filesystem, SessionInfo session) {
		logger.log(Level.INFO, "Initializing client plugins");
		JavaScriptObject env = pluginEnv.getJsEnv(filesystem,
				session.getPluginBaseUrl());
		for (Plugin p : plugins)
			p.initialize(env);
	}

	private native void setupPlugins() /*-{
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
