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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.FileItemUserPermission;
import org.sjarvela.mollify.client.session.FilePermissionMode;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.Formatter;

public class PermissionEditorPresenter {
	private final FileSystemItem item;
	private final FileSystemService service;
	private final PermissionEditorView view;
	private final DialogManager dialogManager;

	public PermissionEditorPresenter(PermissionEditorView view,
			FileSystemService service, FileSystemItem item,
			DialogManager dialogManager,
			Formatter<FilePermissionMode> filePermissionFormatter) {
		this.view = view;
		this.service = service;
		this.item = item;
		this.dialogManager = dialogManager;

		view.getDefaultPermission().setFormatter(filePermissionFormatter);
	}

	public void initialize() {
		view.getItemName().setText(item.getName());

		view.getDefaultPermission().setContent(
				Arrays.asList(FilePermissionMode.values()));

		view.getList().removeAllRows();
		view.showProgress(true);

		service.getItemPermissions(item,
				new ResultListener<List<FileItemUserPermission>>() {
					public void onFail(ServiceError error) {
						view.showProgress(false);
						dialogManager.showError(error);
					}

					public void onSuccess(List<FileItemUserPermission> result) {
						view.showProgress(false);
						updatePermissions(result);
					}
				});
	}

	private void updatePermissions(List<FileItemUserPermission> permissions) {
		List<FileItemUserPermission> userSpecific = new ArrayList();
		boolean defaultPermissionFound = false;

		for (FileItemUserPermission permission : permissions) {
			if (permission.getUser() != null) {
				userSpecific.add(permission);
			} else {
				if (defaultPermissionFound) {
					dialogManager.showError(new ServiceError(
							ServiceErrorType.INVALID_RESPONSE));
					return;
				}
				defaultPermissionFound = true;
				view.getDefaultPermission().setSelectedItem(
						permission.getPermission());
			}
		}
		if (!defaultPermissionFound)
			view.getDefaultPermission()
					.setSelectedItem(FilePermissionMode.None);
		view.getList().setContent(userSpecific);
	}

	public void onClose() {
		view.hide();
	}
}
