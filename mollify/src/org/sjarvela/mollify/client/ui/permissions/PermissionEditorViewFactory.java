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
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.DialogManager;

public class PermissionEditorViewFactory {
	private final TextProvider textProvider;
	private final SettingsService service;
	private final DialogManager dialogManager;

	public PermissionEditorViewFactory(TextProvider textProvider,
			SettingsService service, DialogManager dialogManager) {
		this.textProvider = textProvider;
		this.service = service;
		this.dialogManager = dialogManager;
	}

	public void show(FileSystemItem item) {
		ActionDelegator actionDelegator = new ActionDelegator();
		PermissionEditorView view = new PermissionEditorView(textProvider,
				actionDelegator);
		PermissionEditorPresenter presenter = new PermissionEditorPresenter(
				view, service, item, dialogManager);
		new PermissionEditorGlue(presenter, view, actionDelegator);
	}

}
