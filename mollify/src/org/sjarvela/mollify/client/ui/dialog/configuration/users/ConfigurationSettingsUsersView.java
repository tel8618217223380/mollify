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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.UserList;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationSettingsView;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationSettingsUsersView extends ConfigurationSettingsView {
	private final UserList list;
	private final ActionButton addUserButton;
	private final ActionButton removeUserButton;
	private ActionButton editUserButton;
	private ActionButton resetPasswordButton;

	public enum Actions implements ResourceId {
		addUser, editUser, removeUser, resetPassword
	}

	public ConfigurationSettingsUsersView(TextProvider textProvider,
			ActionListener actionListener) {
		super(textProvider, StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS);

		list = new UserList(textProvider,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_LIST);
		addUserButton = new ActionButton(textProvider.getStrings()
				.configurationDialogSettingUsersAdd(),
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION_ADD,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION);
		addUserButton.setAction(actionListener, Actions.addUser);

		editUserButton = new ActionButton(textProvider.getStrings()
				.configurationDialogSettingUsersEdit(),
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION_EDIT,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION);
		editUserButton.setAction(actionListener, Actions.editUser);

		removeUserButton = new ActionButton(textProvider.getStrings()
				.configurationDialogSettingUsersRemove(),
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION_REMOVE,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION);
		removeUserButton.setAction(actionListener, Actions.removeUser);

		resetPasswordButton = new ActionButton(textProvider.getStrings()
				.configurationDialogSettingUsersResetPassword(),
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION_RESET_PW,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION);
		resetPasswordButton.setAction(actionListener, Actions.resetPassword);

		add(createList());
		add(createButtons());
	}

	private Widget createList() {
		Panel panel = new FlowPanel();
		panel
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_LIST_PANEL);
		panel.add(list);
		return panel;
	}

	private Widget createButtons() {
		Panel userActions = new FlowPanel();
		userActions
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTIONS);
		userActions.add(addUserButton);
		userActions.add(editUserButton);
		userActions.add(removeUserButton);
		userActions.add(resetPasswordButton);
		return userActions;
	}

	@Override
	public String getTitle() {
		return textProvider.getStrings()
				.configurationDialogSettingUsersViewTitle();
	}

	public UserList list() {
		return list;
	}

	public ActionButton addUserButton() {
		return addUserButton;
	}

	public ActionButton editUserButton() {
		return editUserButton;
	}

	public ActionButton removeUserButton() {
		return removeUserButton;
	}

}
