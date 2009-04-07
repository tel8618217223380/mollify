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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog;

public class ConfigurationSettingsFoldersPresenter {
	private final ConfigurationSettingsFoldersView view;
	private final ConfigurationDialog dialog;
	private final SettingsService service;

	public ConfigurationSettingsFoldersPresenter(SettingsService service,
			ConfigurationDialog dialog, ConfigurationSettingsFoldersView view) {
		this.service = service;
		this.dialog = dialog;
		this.view = view;

		view.list().setSelectionMode(SelectionMode.Single);

		service
				.getFolders(dialog
						.createResultListener(new ResultCallback<List<DirectoryInfo>>() {
							public void onCallback(List<DirectoryInfo> list) {
								setFolders(list);
							}
						}));
	}

	protected void setFolders(List<DirectoryInfo> list) {
		view.list().setContent(list);
	}

	public void onAddFolder() {

	}

	public void onRemoveFolder() {

	}
}
