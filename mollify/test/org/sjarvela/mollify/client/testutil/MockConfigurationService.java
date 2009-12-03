/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.testutil;

import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.UserFolder;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;

public class MockConfigurationService implements ConfigurationService {

	private List<User> users = Collections.EMPTY_LIST;

	public void addFolder(String name, String path,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void addUser(String name, String password, UserPermissionMode mode,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void addUserFolder(User user, FolderInfo dir, String name,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void editFolder(FolderInfo dir, String name, String path,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void editUser(User user, String name, UserPermissionMode mode,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void editUserFolder(User user, UserFolder dir, String name,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void getFolders(ResultListener<List<FolderInfo>> resultListener) {
		// TODO Auto-generated method stub

	}

	public void getUserFolders(User user,
			ResultListener<List<UserFolder>> resultListener) {
		// TODO Auto-generated method stub

	}

	public void getUsers(ResultListener<List<User>> resultListener) {
		resultListener.onSuccess(users);
	}

	public void removeFolder(FolderInfo dir, ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void removeUser(User user, ResultListener<Boolean> resultListener) {
		// TODO Auto-generated method stub

	}

	public void removeUserFolder(User user, UserFolder dir,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener) {
		// TODO Auto-generated method stub
		
	}

	public void resetPassword(User user, String password,
			ResultListener resultListener) {
		// TODO Auto-generated method stub
		
	}
}
