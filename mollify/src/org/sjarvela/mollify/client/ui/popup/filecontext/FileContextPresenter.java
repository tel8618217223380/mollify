/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.popup.filecontext;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.provider.FileDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.Callback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.ActionListener;

public class FileContextPresenter implements ActionListener {
	private final FileContextPopupComponent popup;
	private final FileDetailsProvider fileDetailsProvider;
	private final TextProvider textProvider;

	private FileSystemActionHandler fileActionHandler;
	private FileItemDescriptionHandler descriptionHandler;

	private File file = File.Empty;
	private FileDetails details;

	public FileContextPresenter(FileContextPopupComponent popup,
			SessionInfo session, FileDetailsProvider fileDetailsProvider,
			TextProvider textProvider) {
		this.popup = popup;
		this.fileDetailsProvider = fileDetailsProvider;
		this.textProvider = textProvider;
	}

	public void setFileActionHandler(FileSystemActionHandler actionHandler) {
		this.fileActionHandler = actionHandler;
	}

	public void setFileItemDescriptionHandler(
			FileItemDescriptionHandler descriptionHandler) {
		this.descriptionHandler = descriptionHandler;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;

		popup.getDetails().setOpen(false);
		popup.getFilename().setText(file.getName());
		updateDetails(null);

		fileDetailsProvider.getFileDetails(file,
				new ResultListener<FileDetails>() {
					public void onFail(ServiceError error) {
						popup.getDescription().setText(
								error.getType().getMessage(textProvider));
					}

					public void onSuccess(FileDetails details) {
						updateDetails(details);
					}
				});
	}

	private void updateDetails(FileDetails details) {
		this.details = details;
		this.updateDescription();

		popup.updateDetails(details);
		boolean writable = (details == null ? false : details
				.getFilePermission().canWrite());
		popup.updateButtons(writable);
	}

	private void updateDescription() {
		boolean descriptionDefined = isDescriptionDefined();
		String visibleDescription = descriptionDefined ? details
				.getDescription() : "";

		popup.getDescription().setText(visibleDescription);
		popup.setDescriptionEditable(false, descriptionDefined);
	}

	private boolean isDescriptionDefined() {
		return (details != null && details.getDescription() != null);
	}

	protected void onStartEditDescription() {
		popup.setDescriptionEditable(true, isDescriptionDefined());
	}

	protected void onStopEditDescription() {
		popup.setDescriptionEditable(false, isDescriptionDefined());

		final String description = popup.getDescription().getText();
		this.descriptionHandler.setItemDescription(file, description,
				new Callback() {
					public void onCallback() {
						details.setDescription(description);
						updateDescription();
					}
				});
	}

	protected void onCancelEditDescription() {
		updateDescription();
	}

	protected void onRemoveDescription() {
		this.descriptionHandler.removeItemDescription(file, new Callback() {
			public void onCallback() {
				details.removeDescription();
				updateDescription();
			}
		});
	}

	public void onAction(ResourceId action) {
		if (FileSystemAction.class.equals(action.getClass())) {
			fileActionHandler.onAction(file, (FileSystemAction) action);
			popup.hide();
			return;
		}

		if (FileContextPopupComponent.Action.addDescription.equals(action))
			onStartEditDescription();
		else if (FileContextPopupComponent.Action.editDescription
				.equals(action))
			onStartEditDescription();
		else if (FileContextPopupComponent.Action.cancelEditDescription
				.equals(action))
			onCancelEditDescription();
		else if (FileContextPopupComponent.Action.applyDescription
				.equals(action))
			onStopEditDescription();
		else if (FileContextPopupComponent.Action.removeDescription
				.equals(action))
			onRemoveDescription();
	}

}
