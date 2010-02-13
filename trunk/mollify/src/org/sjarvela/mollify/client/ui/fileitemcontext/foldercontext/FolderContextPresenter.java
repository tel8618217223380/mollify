/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.foldercontext;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemPermissionHandler;
import org.sjarvela.mollify.client.filesystem.provider.FolderDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.Callback;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;

public class FolderContextPresenter implements ActionListener {
	private final FileItemContextComponent popup;
	private final FolderDetailsProvider detailsProvider;
	private final TextProvider textProvider;

	private FileSystemActionHandler fileSystemActionHandler;
	private FileSystemPermissionHandler permissionHandler;
	private FileItemDescriptionHandler descriptionHandler;

	private Folder folder;
	private FolderDetails details;

	public FolderContextPresenter(FileItemContextComponent popup,
			SessionInfo session, FolderDetailsProvider detailsProvider,
			TextProvider textProvider) {
		this.popup = popup;
		this.detailsProvider = detailsProvider;
		this.textProvider = textProvider;
	}

	public void setDirectoryActionHandler(FileSystemActionHandler actionHandler) {
		this.fileSystemActionHandler = actionHandler;
	}

	public void setFileItemDescriptionHandler(
			FileItemDescriptionHandler descriptionHandler) {
		this.descriptionHandler = descriptionHandler;
	}

	public void setPermissionHandler(
			FileSystemPermissionHandler permissionHandler) {
		this.permissionHandler = permissionHandler;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;

		popup.getDetails().setOpen(false);
		popup.getName().setText(folder.getName());
		updateDetails(null);

		detailsProvider.getFolderDetails(folder,
				new ResultListener<FolderDetails>() {
					public void onFail(ServiceError error) {
						popup.getDescription().setText(
								error.getType().getMessage(textProvider));
					}

					public void onSuccess(FolderDetails details) {
						updateDetails(details);
					}
				});
	}

	protected void updateDetails(FolderDetails details) {
		this.popup.reset();
		this.details = details;

		this.updateDescription();
		boolean writable = (details == null ? false : details
				.getFilePermission().canWrite());

		this.popup.updateButtons(writable);
		this.popup.initializeDetailsSection();
	}

	private boolean isDescriptionDefined() {
		return (details != null && details.getDescription() != null);
	}

	private void updateDescription() {
		boolean descriptionDefined = isDescriptionDefined();
		String visibleDescription = descriptionDefined ? details
				.getDescription() : "";

		popup.setDescription(visibleDescription);
		popup.setDescriptionEditable(false, descriptionDefined);
	}

	protected void onStartEditDescription() {
		popup.setDescriptionEditable(true, isDescriptionDefined());
	}

	protected void onApplyDescription() {
		final String description = popup.getDescription().getText();
		if (!this.descriptionHandler.validateDescription(description))
			return;

		popup.setDescriptionEditable(false, true);

		this.descriptionHandler.setItemDescription(folder, description,
				new Callback() {
					public void onCallback() {
						details.setDescription(description);
						updateDescription();
					}
				});
	}

	protected void onCancelEditDescription() {
		popup.setDescriptionEditable(false, isDescriptionDefined());
		updateDescription();
	}

	protected void onRemoveDescription() {
		this.descriptionHandler.removeItemDescription(folder, new Callback() {
			public void onCallback() {
				details.removeDescription();
				updateDescription();
			}
		});
	}

	public void onAction(ResourceId action, Object o) {
		if (FileSystemAction.class.equals(action.getClass())) {
			fileSystemActionHandler.onAction(folder, (FileSystemAction) action,
					popup);
			popup.hide();
			return;
		}

		if (FileItemContextComponent.Action.addDescription.equals(action))
			onStartEditDescription();
		else if (FileItemContextComponent.Action.editDescription.equals(action))
			onStartEditDescription();
		else if (FileItemContextComponent.Action.cancelEditDescription
				.equals(action))
			onCancelEditDescription();
		else if (FileItemContextComponent.Action.applyDescription
				.equals(action))
			onApplyDescription();
		else if (FileItemContextComponent.Action.removeDescription
				.equals(action))
			onRemoveDescription();
		else if (FileItemContextComponent.Action.editPermissions.equals(action)) {
			popup.hide();
			permissionHandler.onEditPermissions(folder);
		}
	}

}
