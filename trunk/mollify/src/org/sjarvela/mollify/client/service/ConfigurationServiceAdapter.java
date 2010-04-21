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

import org.sjarvela.mollify.client.filesystem.FolderDef;
import org.sjarvela.mollify.client.filesystem.UserFolder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListenerFactory;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;
import org.sjarvela.mollify.client.session.user.UsersAndGroups;

public class ConfigurationServiceAdapter implements ConfigurationService {
	private final ConfigurationService service;
	private final ResultListenerFactory resultListenerFactory;

	public ConfigurationServiceAdapter(ConfigurationService service,
			ResultListenerFactory resultListenerFactory) {
		this.service = service;
		this.resultListenerFactory = resultListenerFactory;
	}

	@Override
	public String getAdministrationUrl() {
		return service.getAdministrationUrl();
	}

	@Override
	public void addFolder(String name, String path,
			ResultListener resultListener) {
		service.addFolder(name, path, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void addUser(String name, String password, UserPermissionMode mode,
			ResultListener resultListener) {
		service.addUser(name, password, mode, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void addUserFolder(User user, FolderDef dir, String name,
			ResultListener resultListener) {
		service.addUserFolder(user, dir, name, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener) {
		service.changePassword(oldPassword, newPassword, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void editFolder(FolderDef dir, String name, String path,
			ResultListener resultListener) {
		service.editFolder(dir, name, path, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void editUser(User user, String name, UserPermissionMode mode,
			ResultListener resultListener) {
		service.editUser(user, name, mode, resultListenerFactory
				.createListener(resultListener));

	}

	public void editUserFolder(User user, UserFolder dir, String name,
			ResultListener resultListener) {
		service.editUserFolder(user, dir, name, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void getFolders(ResultListener<List<FolderDef>> resultListener) {
		service
				.getFolders(resultListenerFactory
						.createListener(resultListener));
	}

	@Override
	public void getUserFolders(User user,
			ResultListener<List<UserFolder>> resultListener) {
		service.getUserFolders(user, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void getUsersAndGroups(ResultListener<UsersAndGroups> resultListener) {
		service.getUsersAndGroups(resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void removeFolder(FolderDef dir, ResultListener resultListener) {
		service.removeFolder(dir, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void removeUser(User user, ResultListener<Boolean> resultListener) {
		service.removeUser(user, resultListenerFactory
				.createListener(resultListener));
	}

	@Override
	public void removeUserFolder(User user, UserFolder dir,
			ResultListener resultListener) {
		service.removeUserFolder(user, dir, resultListenerFactory
				.createListener(resultListener));
	}

}
