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

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPluginSystem implements PluginSystem {
	EventDispatcher eventDispatcher;

	@Inject
	public DefaultPluginSystem(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public void setup() {
		this.setupClientPlugins(eventDispatcher);
	}

	private native void setupClientPlugins(EventDispatcher e) /*-{
		if (!$wnd.onMollifyStarted) return;

		$wnd.registerEventHandler = function(cb) {
			e.@org.sjarvela.mollify.client.event.DefaultEventDispatcher::registerEventHandler(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}
		$wnd.onMollifyStarted();
	}-*/;
}
