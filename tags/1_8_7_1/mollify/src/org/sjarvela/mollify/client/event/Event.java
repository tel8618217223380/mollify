/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.event;

import com.google.gwt.core.client.JavaScriptObject;

public class Event extends JavaScriptObject {
	public static Event create(String type, JavaScriptObject o) {
		Event e = JavaScriptObject.createObject().cast();
		e.set(type, o);
		return e;
	}

	private native void set(String type, JavaScriptObject o) /*-{
		this.type = type;
		this.payload = o;
	}-*/;

	protected Event() {
	}
}
