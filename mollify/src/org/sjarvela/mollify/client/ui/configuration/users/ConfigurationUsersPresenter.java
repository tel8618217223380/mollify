/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration.users;

import java.util.List;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserHandler;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialog;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialog.ConfigurationType;
import org.sjarvela.mollify.client.ui.password.PasswordDialogFactory;

public class ConfigurationUsersPresenter implements UserHandler {
	private final ConfigurationUsersView view;
	private final ConfigurationDialog parent;
	private final ConfigurationService service;
	private final TextProvider textProvider;
	private final UserDialogFactory userDialogFactory;
	private final PasswordDialogFactory passwordDialogFactory;

	public ConfigurationUsersPresenter(ConfigurationService service,
			ConfigurationDialog dialog, TextProvider textProvider,
			ConfigurationUsersView view, UserDialogFactory userDialogFactory, PasswordDialogFactory passwordDialogFactory) {
		this.service = service;
		this.parent = dialog;
		this.textProvider = textProvider;
		this.view = view;
		this.userDialogFactory = userDialogFactory;
		this.passwordDialogFactory = passwordDialogFactory;

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
		userDialogFactory.openAddUserDialog(this);
	}

	public void onEditUser() {
		if (view.list().getSelected().size() != 1)
			return;

		User selected = view.list().getSelected().get(0);
		userDialogFactory.openEditUserDialog(this, selected);
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
		passwordDialogFactory.openResetPasswordDialog(selected,
				parent.getPasswordHandler());
	}

	public void addUser(String name, String password, UserPermissionMode mode) {
		service.addUser(name, password, mode, createReloadListener(parent
				.createDataChangeNotifier(ConfigurationType.Users)));
	}

	public void editUser(User user, String name, UserPermissionMode mode) {
		service.editUser(user, name, mode, createReloadListener(parent
				.createDataChangeNotifier(ConfigurationType.Users)));
	}

	private ResultListener createReloadListener(
			final Callback... successCallbacks) {
		return parent.createResultListener(new Callback() {
			public void onCallback() {
				if (successCallbacks.length > 0)
					for (Callback successCallback : successCallbacks)
						successCallback.onCallback();
				reload();
			}
		});
	}
}
