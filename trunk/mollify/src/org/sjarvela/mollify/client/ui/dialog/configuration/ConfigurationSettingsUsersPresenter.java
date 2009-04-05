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

import java.util.List;

import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.session.User;

public class ConfigurationSettingsUsersPresenter {
	private final ConfigurationSettingsUsersView view;

	public ConfigurationSettingsUsersPresenter(SettingsService service,
			ConfigurationDialog dialog, ConfigurationSettingsUsersView view) {
		this.view = view;

		service.getUsers(dialog
				.createResultListener(new ResultCallback<List<User>>() {
					public void onCallback(List<User> list) {
						initialize(list);
					}
				}));
	}

	protected void initialize(List<User> list) {
		view.list().setContent(list);
	}
}
