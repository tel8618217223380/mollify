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
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;

public interface SettingsService {

	void getUsers(ResultListener<List<User>> resultListener);

	void getFolders(ResultListener<List<DirectoryInfo>> resultListener);

	void addUser(String name, String password, PermissionMode mode,
			ResultListener resultListener);

	void removeUser(User selected, ResultListener resultListener);

	void editUser(User user, String name, PermissionMode mode,
			ResultListener resultListener);

}
