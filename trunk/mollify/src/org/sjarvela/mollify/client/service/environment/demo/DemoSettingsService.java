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

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;

public class DemoSettingsService implements SettingsService {

	public void getUsers(ResultListener<List<User>> resultListener) {
		resultListener.onSuccess(Arrays.asList(new User("Test User",
				PermissionMode.Admin), new User("Another Test User",
				PermissionMode.ReadWrite)));
	}

	public void getFolders(ResultListener<List<DirectoryInfo>> resultListener) {
		resultListener.onSuccess(Arrays.asList(new DirectoryInfo(new Directory(
				"", "Example Folder"), "/foo/bar"), new DirectoryInfo(
				new Directory("", "Another Folder"), "/bar/foo")));
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

}
