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

import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.UserFolder;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;
import org.sjarvela.mollify.client.session.user.UsersAndGroups;

public class DemoConfigurationService implements ConfigurationService {

	private final DemoData data;

	public DemoConfigurationService(DemoData data) {
		this.data = data;
	}

	public void getUsersAndGroups(ResultListener<UsersAndGroups> resultListener) {
		resultListener.onSuccess(new UsersAndGroups(data.getUsers(), data
				.getUserGroups()));
	}

	public void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener) {
		resultListener.onSuccess(true);
	}

	public void getFolders(ResultListener<List<FolderInfo>> resultListener) {
		FolderInfo dir1 = FolderInfo.create("1", "Example Folder", "/foo/bar");
		FolderInfo dir2 = FolderInfo.create("2", "Another Folder", "/bar/foo");

		resultListener.onSuccess(Arrays.asList(dir1, dir2));
	}

	public void addUser(String name, String password, UserPermissionMode mode,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void editUser(User user, String name, UserPermissionMode mode,
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

	public void editFolder(FolderInfo dir, String name, String path,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void removeFolder(FolderInfo dir, ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void getUserFolders(User user, ResultListener resultListener) {
		UserFolder dir1 = UserFolder.create("1", "Example Folder", "",
				"/foo/bar");
		UserFolder dir2 = UserFolder.create("2", null, "Another Folder",
				"/bar/foo");

		resultListener.onSuccess(Arrays.asList(dir1, dir2));
	}

	public void addUserFolder(User user, FolderInfo dir, String name,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void editUserFolder(User user, UserFolder dir, String name,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void removeUserFolder(User user, UserFolder dir,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	@Override
	public String getAdministrationUrl() {
		return "http://www.mollify.org";
	}
}
