/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.permissions;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.directorymodel.FileSystemItemProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;

public class PermissionEditorViewFactory {
	private final TextProvider textProvider;
	private final FileSystemService fileSystemService;
	private final DialogManager dialogManager;
	private final ConfigurationService configurationService;
	private final FileSystemItemProvider fileSystemItemProvider;

	public PermissionEditorViewFactory(TextProvider textProvider,
			ConfigurationService configurationService,
			FileSystemService fileSystemService,
			FileSystemItemProvider fileSystemItemProvider,
			DialogManager dialogManager) {
		this.textProvider = textProvider;
		this.configurationService = configurationService;
		this.fileSystemService = fileSystemService;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.dialogManager = dialogManager;
	}

	public void show(FileSystemItem item) {
		ActionDelegator actionDelegator = new ActionDelegator();
		PermissionEditorModel model = new PermissionEditorModel(item,
				configurationService, fileSystemService);
		PermissionEditorView view = new PermissionEditorView(textProvider,
				actionDelegator, item != null ? PermissionEditorView.Mode.Fixed
						: PermissionEditorView.Mode.ItemSelectable);
		PermissionEditorPresenter presenter = new PermissionEditorPresenter(
				textProvider, model, view, dialogManager,
				new FilePermissionModeFormatter(textProvider),
				fileSystemItemProvider);
		new PermissionEditorGlue(presenter, view, actionDelegator);
	}

}
