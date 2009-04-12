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

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.Callback;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog.ConfigurationType;

public class ConfigurationUsersPresenter implements UserHandler {
	private final ConfigurationUsersView view;
	private final ConfigurationDialog parent;
	private final SettingsService service;
	private final TextProvider textProvider;

	public ConfigurationUsersPresenter(SettingsService service,
			ConfigurationDialog dialog, TextProvider textProvider,
			ConfigurationUsersView view) {
		this.service = service;
		this.parent = dialog;
		this.textProvider = textProvider;
		this.view = view;

		view.list().setSelectionMode(SelectionMode.Single);
		reload();
	}

	private void reload() {
		parent.setLoading(true);

		service.getUsers(parent
				.createResultListener(new ResultCallback<List<User>>() {
					public void onCallback(List<User> list) {
						view.list().setContent(list);
					}
				}));
	}

	public void onAddUser() {
		parent.getDialogManager().openAddUserDialog(this);
	}

	public void onEditUser() {
		if (view.list().getSelected().size() != 1)
			return;

		User selected = view.list().getSelected().get(0);
		parent.getDialogManager().openEditUserDialog(this, selected);
	}

	public void onRemoveUser() {
		if (view.list().getSelected().size() != 1)
			return;

		User selected = view.list().getSelected().get(0);
		if (selected.getId().equals(parent.getSessionInfo().getLoggedUserId())) {
			parent
					.getDialogManager()
					.showInfo(
							textProvider.getStrings()
									.configurationDialogSettingUsers(),
							textProvider
									.getStrings()
									.configurationDialogSettingUsersCannotDeleteYourself());
			return;
		}
		service.removeUser(selected, createReloadListener(parent
				.createDataChangeNotifier(ConfigurationType.Users)));
	}

	public void onResetPassword() {
		if (view.list().getSelected().size() != 1)
			return;

		User selected = view.list().getSelected().get(0);
		parent.getDialogManager().openResetPasswordDialog(selected,
				parent.getPasswordHandler());
	}

	public void addUser(String name, String password, PermissionMode mode) {
		service.addUser(name, password, mode, createReloadListener(parent
				.createDataChangeNotifier(ConfigurationType.Users)));
	}

	public void editUser(User user, String name, PermissionMode mode) {
		service.editUser(user, name, mode, createReloadListener(parent
				.createDataChangeNotifier(ConfigurationType.Users)));
	}

	private ResultListener createReloadListener(
			final Callback... operationSuccessfulCallbacks) {
		return parent.createResultListener(new Callback() {
			public void onCallback() {
				if (operationSuccessfulCallbacks.length > 0)
					for (Callback operationSuccessfulCallback : operationSuccessfulCallbacks)
						operationSuccessfulCallback.onCallback();
				reload();
			}
		});
	}
}
