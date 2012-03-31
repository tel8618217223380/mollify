/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session.file;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.session.file.js.JsFileItemUserPermission;
import org.sjarvela.mollify.client.session.user.UserBase;
import org.sjarvela.mollify.client.session.user.UserCache;

import com.google.gwt.core.client.JsArray;

public class FileItemUserPermission {
	private final FileSystemItem item;
	private final UserBase userOrGroup;
	private final FilePermission permission;

	public static List<FileItemUserPermission> convert(
			List<JsFileItemUserPermission> permissions, UserCache userCache,
			FileSystemItemCache itemCache) {
		List<FileItemUserPermission> result = new ArrayList();

		for (JsFileItemUserPermission jsPermission : permissions) {
			UserBase userOrGroup = jsPermission.isDefault() ? null : userCache
					.getById(jsPermission.getUserOrGroupId());
			FileSystemItem item = itemCache.getItem(jsPermission.getItemId());
			FilePermission permission = jsPermission.getPermission();

			result
					.add(new FileItemUserPermission(item, userOrGroup,
							permission));
		}
		return result;
	}

	public static JsArray<JsFileItemUserPermission> asJsArray(
			List<FileItemUserPermission> list) {
		JsArray result = JsArray.createArray().cast();
		int index = 0;
		for (FileItemUserPermission permission : list)
			result.set(index++, permission.asJsObj());
		return result;
	}

	public JsFileItemUserPermission asJsObj() {
		return JsFileItemUserPermission.create(item.getId(),
				userOrGroup == null ? null : userOrGroup.getId(), permission);
	}

	public FileItemUserPermission(FileSystemItem item, UserBase userOrGroup,
			FilePermission permission) {
		this.item = item;
		this.userOrGroup = userOrGroup;
		this.permission = permission;
	}

	public FileSystemItem getFileSystemItem() {
		return item;
	}

	public UserBase getUserOrGroup() {
		return userOrGroup;
	}

	public FilePermission getPermission() {
		return permission;
	}

}
