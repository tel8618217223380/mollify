/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session.user;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class UserBase extends JavaScriptObject {

	protected UserBase() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native boolean isGroup() /*-{
		if (this["is_group"] && this["is_group"] == 1) return true;
		return false;
	}-*/;

	protected final native void putValues(String id, String name, boolean group) /*-{
		this.id = id;
		this.name = name;
		this["is_group"] = group ? 1 : 0;
	}-*/;

}
