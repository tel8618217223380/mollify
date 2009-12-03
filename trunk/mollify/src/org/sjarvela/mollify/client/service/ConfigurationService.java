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

import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.UserFolder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;

public interface ConfigurationService {

	void getUsers(ResultListener<List<User>> resultListener);

	void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener);

	void resetPassword(User user, String password, ResultListener resultListener);

	void getFolders(ResultListener<List<FolderInfo>> resultListener);

	void addUser(String name, String password, UserPermissionMode mode,
			ResultListener resultListener);

	void removeUser(User user, ResultListener<Boolean> resultListener);

	void editUser(User user, String name, UserPermissionMode mode,
			ResultListener resultListener);

	void addFolder(String name, String path, ResultListener resultListener);

	void removeFolder(FolderInfo dir, ResultListener resultListener);

	void editFolder(FolderInfo dir, String name, String path,
			ResultListener resultListener);

	void getUserFolders(User user,
			ResultListener<List<UserFolder>> resultListener);

	void addUserFolder(User user, FolderInfo dir, String name,
			ResultListener resultListener);

	void editUserFolder(User user, UserFolder dir, String name,
			ResultListener resultListener);

	void removeUserFolder(User user, UserFolder dir,
			ResultListener resultListener);
}
