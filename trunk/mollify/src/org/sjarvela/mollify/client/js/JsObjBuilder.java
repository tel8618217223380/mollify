/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.js;

import com.google.gwt.core.client.JavaScriptObject;

public class JsObjBuilder {
	JavaScriptObject obj = JavaScriptObject.createObject();

	public JsObjBuilder string(String name, String value) {
		put(obj, name, value);
		return this;
	}

	private native final void put(JavaScriptObject obj, String name,
			String value) /*-{
		obj[name] = value;
	}-*/;

	public JsObj create() {
		return obj.cast();
	}

	public JsObjBuilder obj(String id, JavaScriptObject o) {
		putO(obj, id, o);
		return this;
	}

	private native final void putO(JavaScriptObject obj, String name,
			JavaScriptObject value) /*-{
		obj[name] = value;
	}-*/;
}
