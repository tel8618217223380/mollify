/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration;

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialog.ConfigurationType;
import org.sjarvela.mollify.client.ui.configuration.folders.ConfigurationFoldersGlue;
import org.sjarvela.mollify.client.ui.configuration.folders.ConfigurationFoldersPresenter;
import org.sjarvela.mollify.client.ui.configuration.folders.ConfigurationFoldersView;
import org.sjarvela.mollify.client.ui.configuration.folders.ConfigurationUserFoldersGlue;
import org.sjarvela.mollify.client.ui.configuration.folders.ConfigurationUserFoldersPresenter;
import org.sjarvela.mollify.client.ui.configuration.folders.ConfigurationUserFoldersView;
import org.sjarvela.mollify.client.ui.configuration.users.ConfigurationUsersGlue;
import org.sjarvela.mollify.client.ui.configuration.users.ConfigurationUsersPresenter;
import org.sjarvela.mollify.client.ui.configuration.users.ConfigurationUsersView;

public class ConfigurationViewManager {
	private final ConfigurationService service;
	private final TextProvider textProvider;
	private final ConfigurationDialog dialog;
	private final Map<ResourceId, Configurator> cache = new HashMap();

	public ConfigurationViewManager(TextProvider textProvider,
			ConfigurationService service, ConfigurationDialog dialog) {
		this.textProvider = textProvider;
		this.service = service;
		this.dialog = dialog;
	}

	public Configurator getView(ResourceId id) {
		if (!cache.containsKey(id))
			cache.put(id, createConfigurator(id));
		return cache.get(id);
	}

	private Configurator createConfigurator(ResourceId id) {
		if (id.equals(ConfigurationType.Users))
			return createUsersView();
		else if (id.equals(ConfigurationType.Folders))
			return createFoldersView();
		else if (id.equals(ConfigurationType.UserFolders))
			return createUserFoldersView();
		return null;
	}

	private Configurator createUsersView() {
		ActionDelegator actionDelegator = new ActionDelegator();
		ConfigurationUsersView view = new ConfigurationUsersView(
				textProvider, actionDelegator);
		ConfigurationUsersPresenter presenter = new ConfigurationUsersPresenter(
				service, dialog, textProvider, view);
		return new ConfigurationUsersGlue(view, presenter,
				actionDelegator);
	}

	private Configurator createFoldersView() {
		ActionDelegator actionDelegator = new ActionDelegator();
		ConfigurationFoldersView view = new ConfigurationFoldersView(
				textProvider, actionDelegator);
		ConfigurationFoldersPresenter presenter = new ConfigurationFoldersPresenter(
				service, textProvider, dialog, view);
		return new ConfigurationFoldersGlue(view, presenter,
				actionDelegator);
	}

	private Configurator createUserFoldersView() {
		ActionDelegator actionDelegator = new ActionDelegator();
		ConfigurationUserFoldersView view = new ConfigurationUserFoldersView(
				textProvider, actionDelegator);
		ConfigurationUserFoldersPresenter presenter = new ConfigurationUserFoldersPresenter(
				service, textProvider, dialog, view);
		return new ConfigurationUserFoldersGlue(view, presenter,
				actionDelegator);
	}

	public void onDataChanged(ConfigurationType type) {
		for (Configurator config : cache.values()) {
			config.onDataChanged(type);
		}
	}
}
