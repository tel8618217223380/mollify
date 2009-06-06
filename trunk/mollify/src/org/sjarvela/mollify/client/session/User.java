/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session;

import com.google.gwt.core.client.JavaScriptObject;

public class User extends JavaScriptObject {
	public static User create(String id, String name,
			UserPermissionMode permissionMode) {
		User result = User.createObject().cast();
		result.putValues(id, name, permissionMode.getStringValue());
		return result;
	}

	protected User() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	private final native String getPermissionString() /*-{
		return this.permission_mode;
	}-*/;

	public final UserPermissionMode getType() {
		return UserPermissionMode.fromString(getPermissionString());
	}

	private final native void putValues(String id, String name,
			String permissionMode) /*-{
		this.id = id;
		this.name = name;
		this.permission_mode = permissionMode;
	}-*/;
}
