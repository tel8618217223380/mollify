/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.UserDirectory;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.FileItemUserPermission;
import org.sjarvela.mollify.client.session.UserPermissionMode;
import org.sjarvela.mollify.client.session.User;

public interface SettingsService {

	void getUsers(ResultListener<List<User>> resultListener);

	void getFolders(ResultListener<List<DirectoryInfo>> resultListener);

	void addUser(String name, String password, UserPermissionMode mode,
			ResultListener resultListener);

	void removeUser(User user, ResultListener<Boolean> resultListener);

	void editUser(User user, String name, UserPermissionMode mode,
			ResultListener resultListener);

	void addFolder(String name, String path, ResultListener resultListener);

	void removeFolder(DirectoryInfo dir, ResultListener resultListener);

	void editFolder(DirectoryInfo dir, String name, String path,
			ResultListener resultListener);

	void getUserFolders(User user,
			ResultListener<List<UserDirectory>> resultListener);

	void addUserFolder(User user, DirectoryInfo dir, String name,
			ResultListener resultListener);

	void editUserFolder(User user, UserDirectory dir, String name,
			ResultListener resultListener);

	void removeUserFolder(User user, UserDirectory dir,
			ResultListener resultListener);

	void getItemPermissions(FileSystemItem item,
			ResultListener<List<FileItemUserPermission>> resultListener);
}
