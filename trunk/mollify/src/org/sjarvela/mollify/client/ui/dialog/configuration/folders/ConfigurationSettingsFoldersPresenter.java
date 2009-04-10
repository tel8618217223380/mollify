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
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.Callback;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog;

public class ConfigurationSettingsFoldersPresenter implements FolderHandler {
	private final ConfigurationSettingsFoldersView view;
	private final ConfigurationDialog dialog;
	private final SettingsService service;
	private final TextProvider textProvider;

	public ConfigurationSettingsFoldersPresenter(SettingsService service,
			TextProvider textProvider, ConfigurationDialog dialog,
			ConfigurationSettingsFoldersView view) {
		this.service = service;
		this.textProvider = textProvider;
		this.dialog = dialog;
		this.view = view;

		view.list().setSelectionMode(SelectionMode.Single);
		reload();
	}

	private void reload() {
		service
				.getFolders(dialog
						.createResultListener(new ResultCallback<List<DirectoryInfo>>() {
							public void onCallback(List<DirectoryInfo> list) {
								view.list().setContent(list);
							}
						}));
	}

	public void onAddFolder() {
		new FolderDialog(textProvider, this);
	}

	public void onEditFolder() {
		if (view.list().getSelected().size() != 1)
			return;

		new FolderDialog(textProvider, this, view.list().getSelected().get(0));
	}

	public void onRemoveFolder() {
		if (view.list().getSelected().size() != 1)
			return;

		service.removeFolder(view.list().getSelected().get(0),
				createReloadListener());
	}

	public void addFolder(String name, String path) {
		service.addFolder(name, path, createReloadListener());
	}

	public void editFolder(DirectoryInfo folder, String name, String path) {
		service.editFolder(folder, name, path, createReloadListener());
	}

	private ResultListener createReloadListener() {
		return dialog.createResultListener(new Callback() {
			public void onCallback() {
				reload();
			}
		});
	}
}
