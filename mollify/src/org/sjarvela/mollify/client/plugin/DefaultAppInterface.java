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

import java.util.logging.Logger;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.session.SessionInfo;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultAppInterface implements AppInterface {
	private static Logger logger = Logger.getLogger(DefaultAppInterface.class
			.getName());

	private final ClientInterface clientInterface;

	// TODO move this entire class into external js
	@Inject
	public DefaultAppInterface(ClientInterface clientInterface) {
		this.clientInterface = clientInterface;
	}

	@Override
	public void setup(final SessionInfo session, final Callback onReady) {
		JavaScriptObject env = clientInterface.asJs(session.getPluginBaseUrl());
		initLib(env, onReady);
	}

	// private Map<String, String> getExternalPluginScripts(SessionInfo session)
	// {
	// logger.log(Level.INFO, "Initializing client plugins from session");
	// JavaScriptObject pluginsObj = session.getPlugins();
	// if (pluginsObj == null)
	// return Collections.EMPTY_MAP;
	//
	// Map<String, String> result = new HashMap();
	// JsObj plugins = pluginsObj.cast();
	// for (String id : plugins.getKeys()) {
	// if (id == null || id.length() == 0 || id.startsWith("_"))
	// continue;
	// logger.log(Level.INFO, "Initializing client plugin " + id);
	// JsObj plugin = plugins.getJsObj(id).cast();
	//
	// if (plugin.hasValue("client_plugin"))
	// result.put(id, plugin.getString("client_plugin"));
	// }
	// return result;
	// }

	private native void initLib(JavaScriptObject env, Callback cb) /*-{
		if (!$wnd.mollify)
			return;
		$wnd.mollify
				.setup(
						env,
						function() {
							cb.@org.sjarvela.mollify.client.Callback::onCallback()();
						});
	}-*/;
}
