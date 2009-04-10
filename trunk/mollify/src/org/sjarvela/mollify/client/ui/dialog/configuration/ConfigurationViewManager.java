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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog.Settings;
import org.sjarvela.mollify.client.ui.dialog.configuration.folders.ConfigurationSettingsFoldersGlue;
import org.sjarvela.mollify.client.ui.dialog.configuration.folders.ConfigurationSettingsFoldersPresenter;
import org.sjarvela.mollify.client.ui.dialog.configuration.folders.ConfigurationSettingsFoldersView;
import org.sjarvela.mollify.client.ui.dialog.configuration.users.ConfigurationSettingsUsersGlue;
import org.sjarvela.mollify.client.ui.dialog.configuration.users.ConfigurationSettingsUsersPresenter;
import org.sjarvela.mollify.client.ui.dialog.configuration.users.ConfigurationSettingsUsersView;

public class ConfigurationViewManager {
	private final SettingsService service;
	private final TextProvider textProvider;
	private final ConfigurationDialog dialog;

	public ConfigurationViewManager(TextProvider textProvider,
			SettingsService service, ConfigurationDialog dialog) {
		this.textProvider = textProvider;
		this.service = service;
		this.dialog = dialog;
	}

	public ConfigurationSettingsView createView(ResourceId id) {
		if (id.equals(Settings.Users))
			return createUsersView();
		else if (id.equals(Settings.Folders))
			return createFoldersView();

		return null;
	}

	private ConfigurationSettingsView createUsersView() {
		ActionDelegator actionDelegator = new ActionDelegator();
		ConfigurationSettingsUsersView view = new ConfigurationSettingsUsersView(
				textProvider, actionDelegator);
		ConfigurationSettingsUsersPresenter presenter = new ConfigurationSettingsUsersPresenter(
				service, dialog, textProvider, view);
		new ConfigurationSettingsUsersGlue(view, presenter, actionDelegator);
		return view;
	}

	private ConfigurationSettingsView createFoldersView() {
		ActionDelegator actionDelegator = new ActionDelegator();
		ConfigurationSettingsFoldersView view = new ConfigurationSettingsFoldersView(
				textProvider, actionDelegator);
		ConfigurationSettingsFoldersPresenter presenter = new ConfigurationSettingsFoldersPresenter(
				service, textProvider, dialog, view);
		new ConfigurationSettingsFoldersGlue(view, presenter, actionDelegator);
		return view;
	}
}
