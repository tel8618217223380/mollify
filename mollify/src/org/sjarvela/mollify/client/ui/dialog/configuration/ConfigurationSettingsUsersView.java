/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.UserList;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;

public class ConfigurationSettingsUsersView extends ConfigurationSettingsView {
	private UserList list;

	public ConfigurationSettingsUsersView(TextProvider textProvider) {
		super(textProvider, StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS);
		list = new UserList(textProvider,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USERS_LIST);
		list.setSelectionMode(SelectionMode.Single);

		add(list);
	}

	@Override
	public String getTitle() {
		return textProvider.getStrings()
				.configurationDialogSettingUsersViewTitle();
	}

	public UserList list() {
		return list;
	}
}
