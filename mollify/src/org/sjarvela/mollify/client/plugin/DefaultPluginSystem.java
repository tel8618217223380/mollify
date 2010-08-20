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

import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.service.request.HttpResponseProcessor;
import org.sjarvela.mollify.client.service.request.HttpResponseProcessorProxy;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPluginSystem implements PluginSystem {
	private final EventDispatcher eventDispatcher;
	private final HttpResponseProcessorProxy httpResponseProcessorProxy;

	@Inject
	public DefaultPluginSystem(EventDispatcher eventDispatcher,
			HttpResponseProcessorProxy httpResponseProcessorProxy) {
		this.eventDispatcher = eventDispatcher;
		this.httpResponseProcessorProxy = httpResponseProcessorProxy;
	}

	@Override
	public void setup() {
		this.setupClientPlugins(eventDispatcher, this);
	}

	private native void setupClientPlugins(EventDispatcher e, PluginSystem p) /*-{
		if (!$wnd.onMollifyStarted) return;

		$wnd.registerEventHandler = function(cb) {
			e.@org.sjarvela.mollify.client.event.DefaultEventDispatcher::registerEventHandler(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		$wnd.registerPlugin = function(plugin) {
			p.@org.sjarvela.mollify.client.plugin.DefaultPluginSystem::onRegisterPlugin(Lcom/google/gwt/core/client/JavaScriptObject;)(plugin);
		}
		$wnd.onMollifyStarted();
	}-*/;

	public void onRegisterPlugin(JavaScriptObject p) {
		if (p == null)
			return;
		Plugin plugin = p.cast();
		if (plugin.getType() == null)
			return;

		if (plugin.getType().equalsIgnoreCase("response")) {
			final ResponsePlugin responsePlugin = p.cast();
			httpResponseProcessorProxy
					.addProcessor(new HttpResponseProcessor() {
						@Override
						public String processHttpResult(String response) {
							return responsePlugin.processResponse(response);
						}
					});
		}
	};
}
