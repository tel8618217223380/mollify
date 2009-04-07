/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.php;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;

public class PhpSettingsService implements SettingsService {
	private final PhpService service;

	public PhpSettingsService(PhpService service) {
		this.service = service;
	}

	public void getUsers(ResultListener<List<User>> resultListener) {
		// TODO Auto-generated method stub

	}

	public void getFolders(ResultListener<List<DirectoryInfo>> resultListener) {
		// TODO Auto-generated method stub

	}

	public void addUser(String name, String password, PermissionMode mode,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void editUser(User user, String name, PermissionMode mode,
			ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

	public void removeUser(User selected, ResultListener resultListener) {
		// TODO Auto-generated method stub

	}

}
