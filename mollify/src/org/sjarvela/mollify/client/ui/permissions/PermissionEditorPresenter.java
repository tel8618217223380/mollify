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

import java.util.Arrays;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.session.FileItemUserPermission;
import org.sjarvela.mollify.client.session.FilePermissionMode;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.Formatter;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;

public class PermissionEditorPresenter {
	private final PermissionEditorView view;
	private final DialogManager dialogManager;
	private final PermissionEditorModel model;

	public PermissionEditorPresenter(PermissionEditorModel model,
			PermissionEditorView view, DialogManager dialogManager,
			Formatter<FilePermissionMode> filePermissionFormatter) {
		this.model = model;
		this.view = view;
		this.dialogManager = dialogManager;

		model.setErrorCallback(new ResultCallback<ServiceError>() {
			public void onCallback(ServiceError error) {
				onError(error);
			}
		});

		view.getList().setSelectionMode(SelectionMode.Single);
		view.getList().setPermissionFormatter(filePermissionFormatter);

		view.getDefaultPermission().setFormatter(filePermissionFormatter);
	}

	protected void onError(ServiceError error) {
		view.showProgress(false);
		dialogManager.showError(error);
	}

	public void initialize() {
		view.getItemName().setText(model.getItem().getName());

		view.getDefaultPermission().setContent(
				Arrays.asList(FilePermissionMode.values()));

		view.getList().removeAllRows();
		view.showProgress(true);

		model.refresh(new Callback() {
			public void onCallback() {
				view.showProgress(false);
				updatePermissions();
			}
		});
	}

	private void updatePermissions() {
		view.getDefaultPermission().setSelectedItem(
				model.getDefaultPermission());
		refreshList();
	}

	private void refreshList() {
		view.getList().setContent(model.getUserSpecificPermissions());
	}

	public void onOk() {
		if (!model.hasChanged()) {
			onClose();
			return;
		}

		model.commit(new Callback() {
			public void onCallback() {
				onClose();
			}
		});
	}

	public void onClose() {
		view.hide();
	}

	public void onAddPermission() {
		model.addPermission(FileItemUserPermission.create(model.getItem(),
				model.getUsers().get(0), FilePermissionMode.ReadWrite));
		refreshList();
	}

	public void onEditPermission() {
		model.editPermission(model.getUserSpecificPermissions().get(0));
	}

	public void onRemovePermission() {
		model.removePermission(model.getUserSpecificPermissions().get(0));
		refreshList();
	}
}
