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

public class FileItemUserPermission extends JavaScriptObject {
	public static FileItemUserPermission create(String itemId, User user,
			FilePermissionMode permission) {
		FileItemUserPermission result = FileItemUserPermission.createObject()
				.cast();
		result.putValues(itemId, user, permission.getStringValue());
		return result;
	}

	private final native void putValues(String itemId, User user,
			String permission) /*-{
		this.item_id = itemId;
		this.user = user;
		this.permission = permission;
	}-*/;

	protected FileItemUserPermission() {
	}

	public final native String getItemId() /*-{
		return this.item_id;
	}-*/;

	public final native User getUser() /*-{
		return this.user;
	}-*/;

	private final native String getPermissionString() /*-{
		return this.permission;
	}-*/;

	public final FilePermissionMode getPermission() {
		return FilePermissionMode.fromString(getPermissionString());
	}
}
