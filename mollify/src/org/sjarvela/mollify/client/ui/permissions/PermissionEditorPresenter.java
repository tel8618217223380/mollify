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
import java.util.List;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.directorymodel.FileSystemItemProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileItemUserPermissionHandler;
import org.sjarvela.mollify.client.session.file.FilePermissionMode;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.Formatter;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.dialog.SelectItemHandler;

public class PermissionEditorPresenter implements FileItemUserPermissionHandler {
	private final PermissionEditorView view;
	private final DialogManager dialogManager;
	private final PermissionEditorModel model;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final TextProvider textProvider;

	public PermissionEditorPresenter(TextProvider textProvider,
			PermissionEditorModel model, PermissionEditorView view,
			DialogManager dialogManager,
			Formatter<FilePermissionMode> filePermissionFormatter,
			FileSystemItemProvider fileSystemItemProvider) {
		this.textProvider = textProvider;
		this.model = model;
		this.view = view;
		this.dialogManager = dialogManager;
		this.fileSystemItemProvider = fileSystemItemProvider;

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
		view.getDefaultPermission().setContent(
				Arrays.asList(FilePermissionMode.values()));

		updateView();
	}

	private void updateView() {
		view.updateControls(false);

		if (!model.hasItem())
			return;

		view.getItemName().setText(model.getItem().getName());

		view.getList().removeAllRows();
		view.showProgress(true);

		model.refresh(new Callback() {
			public void onCallback() {
				view.showProgress(false);
				updatePermissions();
				view.updateControls(true);
			}
		});
	}

	private void updateItem(FileSystemItem item) {
		model.setItem(item);
		updateView();
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
		if (!model.hasItem())
			return;

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
		List<User> availableUsers = model.getUsersWithoutPermission();
		if (availableUsers.size() == 0)
			return;
		dialogManager.openAddFileItemUserPermissionDialog(this, availableUsers);
	}

	public void onEditPermission() {
		List<FileItemUserPermission> selected = view.getList().getSelected();
		if (selected.size() != 1)
			return;

		dialogManager.openEditFileItemUserPermissionDialog(this, selected
				.get(0));
	}

	public void onRemovePermission() {
		List<FileItemUserPermission> selected = view.getList().getSelected();
		if (selected.size() != 1)
			return;
		model.removePermission(selected.get(0));
		refreshList();
	}

	public void addFileItemUserPermission(User user,
			FilePermissionMode permission) {
		model.addPermission(user, permission);
		refreshList();
	}

	public void editFileItemUserPermission(FileItemUserPermission permission) {
		model.editPermission(permission);
		refreshList();
	}

	public void onDefaultPermissionChanged(FilePermissionMode defaultPermission) {
		model.setDefaultPermission(defaultPermission);
	}

	public void onSelectItem() {
		if (model.hasChanged())
			dialogManager.showConfirmationDialog(textProvider.getStrings()
					.itemPermissionEditorDialogTitle(), textProvider
					.getStrings().itemPermissionEditorConfirmItemChange(),
					StyleConstants.CONFIRMATION_DIALOG_TYPE_OVERRIDE,
					new ConfirmationListener() {
						public void onConfirm() {
							openSelectItemDialog();
						}
					});
		else
			openSelectItemDialog();
	}

	protected void openSelectItemDialog() {
		dialogManager.showSelectItemDialog(textProvider.getStrings()
				.selectItemDialogTitle(), textProvider.getStrings()
				.selectPermissionItemDialogMessage(), textProvider.getStrings()
				.selectPermissionItemDialogAction(), fileSystemItemProvider,
				new SelectItemHandler() {
					public boolean isItemAllowed(FileSystemItem item,
							List<Directory> path) {
						return true;
					}

					public void onSelect(FileSystemItem selected) {
						updateItem(selected);
					}
				});
	}

}
