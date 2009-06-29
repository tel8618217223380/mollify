/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration.folders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.filesystem.UserDirectory;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialog;

public class ConfigurationUserFoldersPresenter implements UserFolderHandler {
	private ConfigurationService service;
	private TextProvider textProvider;
	private ConfigurationDialog parent;
	private ConfigurationUserFoldersView view;

	private List<DirectoryInfo> allDirectories = null;
	private List<User> users = null;

	User selectedUser = null;
	List<UserDirectory> userDirectories = null;
	Map<String, DirectoryInfo> userDirectoryMap = new HashMap();

	public ConfigurationUserFoldersPresenter(ConfigurationService service,
			TextProvider textProvider, ConfigurationDialog dialog,
			ConfigurationUserFoldersView view) {
		this.service = service;
		this.textProvider = textProvider;
		this.parent = dialog;
		this.view = view;

		view.directories().setSelectionMode(SelectionMode.Single);
		reload();
	}

	private void reload() {
		parent.setLoading(true);

		service.getUsers(parent
				.createResultListener(new ResultCallback<List<User>>() {
					public void onCallback(List<User> list) {
						refreshUsers(list);
						reloadFolders();
					}
				}));
	}

	void reloadUsers() {
		parent.setLoading(true);

		service.getUsers(parent
				.createResultListener(new ResultCallback<List<User>>() {
					public void onCallback(List<User> list) {
						refreshUsers(list);
					}
				}));
	}

	void reloadFolders() {
		parent.setLoading(true);

		service
				.getFolders(parent
						.createResultListener(new ResultCallback<List<DirectoryInfo>>() {
							public void onCallback(List<DirectoryInfo> list) {
								allDirectories = list;
							}
						}));
	}

	protected void refreshUsers(List<User> list) {
		this.users = list;
		this.selectedUser = null;
		this.userDirectories = null;

		this.view.user().clear();
		this.view.user().addItem(
				textProvider.getStrings()
						.configurationDialogSettingUserFoldersSelectUser(),
				null);
		for (User user : list)
			this.view.user().addItem(user.getName(), user.getId());

		this.view.user().setSelectedIndex(0);
	}

	public void onUserChanged() {
		selectedUser = null;
		if (view.user().getSelectedIndex() >= 1) {
			selectedUser = users.get(view.user().getSelectedIndex() - 1);
			userDirectories = null;
		}
		refreshUserDirectories();
	}

	private void refreshUserDirectories() {
		if (selectedUser == null) {
			setUserDirectories(new ArrayList<UserDirectory>());
			return;
		}

		service
				.getUserFolders(
						selectedUser,
						parent
								.createResultListener(new ResultCallback<List<UserDirectory>>() {
									public void onCallback(
											List<UserDirectory> list) {
										setUserDirectories(list);
									}

								}));
	}

	private void setUserDirectories(List<UserDirectory> list) {
		this.userDirectories = list;
		this.userDirectoryMap.clear();

		for (UserDirectory dir : list)
			this.userDirectoryMap.put(dir.getId(), dir);
		this.view.directories().setContent(list);
	}

	public void onAddUserFolder() {
		if (allDirectories == null || selectedUser == null)
			return;

		List<DirectoryInfo> selectable = getSelectableDirectories();
		if (selectable.size() == 0) {
			parent
					.getDialogManager()
					.showInfo(
							textProvider
									.getStrings()
									.configurationDialogSettingUserFoldersViewTitle(),
							textProvider
									.getStrings()
									.configurationDialogSettingUserFoldersNoFoldersAvailable());
			return;
		}
		new UserFolderDialog(textProvider, this, selectable);
	}

	private List<DirectoryInfo> getSelectableDirectories() {
		List<DirectoryInfo> result = new ArrayList();
		for (DirectoryInfo directory : allDirectories) {
			if (!userDirectoryMap.containsKey(directory.getId()))
				result.add(directory);
		}
		return result;
	}

	public void onEditUserFolder() {
		if (allDirectories == null || selectedUser == null
				|| view.directories().getSelected().size() != 1)
			return;

		UserDirectory selected = view.directories().getSelected().get(0);
		new UserFolderDialog(textProvider, this, selected);
	}

	public void onRemoveUserFolder() {
		if (allDirectories == null || selectedUser == null
				|| view.directories().getSelected().size() != 1)
			return;

		UserDirectory selected = view.directories().getSelected().get(0);
		service
				.removeUserFolder(selectedUser, selected,
						createReloadListener());
	}

	public void addUserFolder(DirectoryInfo directory, String name,
			Callback successCallback) {
		if (selectedUser == null)
			return;

		service.addUserFolder(selectedUser, directory, name,
				createReloadListener(successCallback));
	}

	public void editUserFolder(UserDirectory edited, String name,
			Callback successCallback) {
		if (selectedUser == null)
			return;

		service.editUserFolder(selectedUser, edited, name,
				createReloadListener(successCallback));
	}

	private ResultListener createReloadListener(
			final Callback... successCallbacks) {
		return parent.createResultListener(new Callback() {
			public void onCallback() {
				if (successCallbacks.length > 0)
					for (Callback callback : successCallbacks)
						callback.onCallback();
				refreshUserDirectories();
			}
		});
	}
}
