/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration.folders;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.UserDirectory;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog;

public class ConfigurationSettingsUserFoldersPresenter {

	private SettingsService service;
	private TextProvider textProvider;
	private ConfigurationDialog dialog;
	private ConfigurationSettingsUserFoldersView view;

	private List<User> users;

	public ConfigurationSettingsUserFoldersPresenter(SettingsService service,
			TextProvider textProvider, ConfigurationDialog dialog,
			ConfigurationSettingsUserFoldersView view) {
		this.service = service;
		this.textProvider = textProvider;
		this.dialog = dialog;
		this.view = view;

		view.directories().setSelectionMode(SelectionMode.Single);
		reloadUsers();
	}

	private void reloadUsers() {
		service.getUsers(dialog
				.createResultListener(new ResultCallback<List<User>>() {
					public void onCallback(List<User> list) {
						refreshUsers(list);
					}
				}));
	}

	protected void refreshUsers(List<User> list) {
		view.user().clear();
		this.users = list;

		view.user().addItem(
				textProvider.getStrings()
						.configurationDialogSettingUserFoldersSelectUser(),
				null);
		for (User user : list)
			view.user().addItem(user.getName(), user.getId());

		view.user().setSelectedIndex(0);
	}

	public void onUserChanged() {
		if (view.user().getSelectedIndex() < 1) {
			view.directories().setContent(new ArrayList<UserDirectory>());
			return;
		}

		User user = users.get(view.user().getSelectedIndex() - 1);
		service
				.getUserFolders(
						user,
						dialog
								.createResultListener(new ResultCallback<List<UserDirectory>>() {
									public void onCallback(
											List<UserDirectory> list) {
										view.directories().setContent(list);
									}
								}));
	}

	public void onAddUserFolder() {

	}

	public void onEditUserFolder() {

	}

	public void onRemoveUserFolder() {

	}
}
