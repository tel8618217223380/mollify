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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.FileItemUserPermission;
import org.sjarvela.mollify.client.ui.DialogManager;

public class PermissionEditorPresenter {

	private final FileSystemItem item;
	private final SettingsService service;
	private final PermissionEditorView view;
	private final DialogManager dialogManager;

	public PermissionEditorPresenter(PermissionEditorView view,
			SettingsService service, FileSystemItem item,
			DialogManager dialogManager) {
		this.view = view;
		this.service = service;
		this.item = item;
		this.dialogManager = dialogManager;
	}

	public void initialize() {
		service.getItemPermissions(item,
				new ResultListener<List<FileItemUserPermission>>() {
					public void onFail(ServiceError error) {
						dialogManager.showError(error);
					}

					public void onSuccess(List<FileItemUserPermission> result) {
						view.getList().setContent(result);
					}
				});
	}

	public void onClose() {
		view.hide();
	}

}
