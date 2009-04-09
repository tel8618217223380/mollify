/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration.users;

import java.util.List;

import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.Callback;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog;

public class ConfigurationSettingsUsersPresenter implements UserHandler {
	private final ConfigurationSettingsUsersView view;
	private final ConfigurationDialog dialog;
	private final SettingsService service;

	public ConfigurationSettingsUsersPresenter(SettingsService service,
			ConfigurationDialog dialog, ConfigurationSettingsUsersView view) {
		this.service = service;
		this.dialog = dialog;
		this.view = view;

		view.list().setSelectionMode(SelectionMode.Single);
		reload();
	}

	private void reload() {
		service.getUsers(dialog
				.createResultListener(new ResultCallback<List<User>>() {
					public void onCallback(List<User> list) {
						setUsers(list);
					}
				}));
	}

	private void setUsers(List<User> list) {
		view.list().setContent(list);
	}

	public void onAddUser() {
		dialog.getDialogManager().openAddUserDialog(this);
	}

	public void onEditUser() {
		if (view.list().getSelected().size() != 1)
			return;

		User selected = view.list().getSelected().get(0);
		dialog.getDialogManager().openEditUserDialog(this, selected);
	}

	public void onRemoveUser() {
		if (view.list().getSelected().size() != 1)
			return;

		User selected = view.list().getSelected().get(0);
		service.removeUser(selected, createReloadListener());
	}

	public void addUser(String name, String password, PermissionMode mode) {
		service.addUser(name, password, mode, createReloadListener());
	}

	public void editUser(User user, String name, PermissionMode mode) {
		service.editUser(user, name, mode, createReloadListener());
	}

	private ResultListener createReloadListener() {
		return dialog.createResultListener(new Callback() {
			public void onCallback() {
				reload();
			}
		});
	}

}
