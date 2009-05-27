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

public class FileItemUserPermission {
	private final FileSystemItem item;
	private final User user;
	private final FilePermissionMode permission;

	public FileItemUserPermission(FileSystemItem item, User user,
			FilePermissionMode permission) {
		this.item = item;
		this.user = user;
		this.permission = permission;
	}

	public FileSystemItem getItem() {
		return item;
	}

	public FilePermissionMode getPermission() {
		return permission;
	}

	public User getUser() {
		return user;
	}
}
