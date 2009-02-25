/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.js;

import com.google.gwt.core.client.JavaScriptObject;

public class JsDirectory extends JavaScriptObject {
	protected JsDirectory() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public static JsDirectory create(String id, String name) {
		JsDirectory result = JsDirectory.createObject().cast();
		result.putValues(id, name);
		return result;
	}

	private final native void putValues(String id, String name) /*-{
		this.id = id;
		this.name = name;
	}-*/;
}
