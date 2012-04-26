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

	public JsObjBuilder bool(String name, boolean v) {
		putB(obj, name, v);
		return this;
	}

	public JsObjBuilder number(String name, int n) {
		putI(obj, name, n);
		return this;
	}

	private native final void putO(JavaScriptObject obj, String name,
			JavaScriptObject value) /*-{
		obj[name] = value;
	}-*/;

	private native final void putB(JavaScriptObject obj, String name,
			boolean value) /*-{
		obj[name] = value;
	}-*/;

	private native final void putI(JavaScriptObject obj, String name, int value) /*-{
		obj[name] = value;
	}-*/;

	public void add(JavaScriptObject o) {
		if (o == null)
			return;
		addO(obj, o);
	}

	private native void addO(JavaScriptObject obj, JavaScriptObject o) /*-{
		for ( var k in o)
			obj[k] = o[k];
	}-*/;

}
