/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration.folders;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.Callback;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.ResultCallback;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialog;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialog.ConfigurationType;

public class ConfigurationFoldersPresenter implements FolderHandler {
	private final ConfigurationFoldersView view;
	private final ConfigurationDialog parent;
	private final ConfigurationService service;
	private final TextProvider textProvider;

	public ConfigurationFoldersPresenter(ConfigurationService service,
			TextProvider textProvider, ConfigurationDialog dialog,
			ConfigurationFoldersView view) {
		this.service = service;
		this.textProvider = textProvider;
		this.parent = dialog;
		this.view = view;

		view.list().setSelectionMode(SelectionMode.Single);
		reload();
	}

	private void reload() {
		parent.setLoading(true);

		service
				.getFolders(parent
						.createResultListener(new ResultCallback<List<FolderInfo>>() {
							public void onCallback(List<FolderInfo> list) {
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
				createReloadListener(parent
						.createDataChangeNotifier(ConfigurationType.Folders)));
	}

	public void addFolder(String name, String path, Callback successCallback) {
		service.addFolder(name, path, createReloadListener(parent
				.createDataChangeNotifier(ConfigurationType.Folders),
				successCallback));
	}

	public void editFolder(FolderInfo folder, String name, String path,
			Callback successCallback) {
		service.editFolder(folder, name, path, createReloadListener(parent
				.createDataChangeNotifier(ConfigurationType.Folders),
				successCallback));
	}

	private ResultListener createReloadListener(
			final Callback... successCallbacks) {
		return parent.createResultListener(new Callback() {
			public void onCallback() {
				if (successCallbacks.length > 0)
					for (Callback callback : successCallbacks)
						callback.onCallback();
				reload();
			}
		});
	}
}
