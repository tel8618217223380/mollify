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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationSettingsUsersView extends ConfigurationSettingsView {
	private final UserList list;
	private final ActionButton addUserButton;
	private final ActionButton removeUserButton;

	public enum Actions implements ResourceId {
		addUser, removeUser
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

		removeUserButton = new ActionButton(textProvider.getStrings()
				.configurationDialogSettingUsersRemove(),
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION_REMOVE,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTION);
		removeUserButton.setAction(actionListener, Actions.removeUser);

		add(createList());
		add(createButtons());
	}

	private Widget createList() {
		return list;
	}

	private Widget createButtons() {
		Panel userActions = new HorizontalPanel();
		userActions
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_ACTIONS);
		userActions.add(addUserButton);
		userActions.add(removeUserButton);
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

	public ActionButton removeUserButton() {
		return removeUserButton;
	}

}
