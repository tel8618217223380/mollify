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

import com.google.gwt.core.client.JavaScriptObject;

public class Plugin extends JavaScriptObject {
	protected Plugin() {
	}

	public final native PluginInfo getPluginInfo() /*-{
		if (!this.getPluginInfo) return null;
		var i = this.getPluginInfo();
		if (!i || i == null) return null;
		return i;
	}-*/;

	public final native void initialize(JavaScriptObject env) /*-{
		this.initialize(env);
	}-*/;
}
