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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;

import com.google.gwt.core.client.JavaScriptObject;

public class FileItemUserPermission extends JavaScriptObject {
	public static FileItemUserPermission create(FileSystemItem item, User user,
			FilePermissionMode permission) {
		FileItemUserPermission result = FileItemUserPermission.createObject()
				.cast();
		result.putValues(item.getId(), user.getId(), permission
				.getStringValue());
		return result;
	}

	private final native void putValues(String itemId, String userId,
			String permission) /*-{
		this.item_id = itemId;
		this.user_id = userId;
		this.permission = permission;
	}-*/;

	protected FileItemUserPermission() {
	}

	public final native String getItemId() /*-{
		return this.item_id;
	}-*/;

	public final native String getUserId() /*-{
		return this.user_id;
	}-*/;

	private final native String getPermissionString() /*-{
		return this.permission;
	}-*/;

	public final FilePermissionMode getPermission() {
		return FilePermissionMode.fromString(getPermissionString());
	}

	public final void setPermission(FilePermissionMode permission) {
		setPermissionString(permission.getStringValue());
	};

	public final native void setPermissionString(String permission) /*-{
		this.permission = permission;
	}-*/;

}
