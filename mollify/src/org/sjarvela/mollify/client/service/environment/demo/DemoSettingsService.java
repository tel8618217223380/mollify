/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.demo;

import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.filesystem.UserDirectory;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;

public class DemoSettingsService implements SettingsService {

	public void getUsers(ResultListener<List<User>> resultListener) {
		resultListener.onSuccess(Arrays.asList(User.create("1", "Test User",
				PermissionMode.Admin), User.create("2", "Another Test User",
				PermissionMode.ReadWrite)));
	}

	public void getFolders(ResultListener<List<DirectoryInfo>> resultListener) {
		DirectoryInfo dir1 = DirectoryInfo.create("1", "Example Folder",
				"/foo/bar");
		DirectoryInfo dir2 = DirectoryInfo.create("2", "Another Folder",
				"/bar/foo");

		resultListener.onSuccess(Arrays.asList(dir1, dir2));
	}

	public void addUser(String name, String password, PermissionMode mode,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void editUser(User user, String name, PermissionMode mode,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void removeUser(User selected, ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void addFolder(String name, String path,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void editFolder(DirectoryInfo dir, String name, String path,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void removeFolder(DirectoryInfo dir, ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void getUserFolders(User user, ResultListener resultListener) {
		UserDirectory dir1 = UserDirectory.create("1", "Example Folder", "",
				"/foo/bar");
		UserDirectory dir2 = UserDirectory.create("2", null, "Another Folder",
				"/bar/foo");

		resultListener.onSuccess(Arrays.asList(dir1, dir2));
	}

	public void addUserFolder(User user, DirectoryInfo dir, String name,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void editUserFolder(User user, UserDirectory dir, String name,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void removeUserFolder(User user, UserDirectory dir,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

}
