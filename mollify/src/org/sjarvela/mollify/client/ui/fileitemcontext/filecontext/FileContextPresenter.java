/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.filecontext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;

import com.google.gwt.i18n.client.DateTimeFormat;

public class FileContextPresenter implements ActionListener {
	private final FileItemContextComponent popup;
	private final FileDetailsProvider fileDetailsProvider;
	private final TextProvider textProvider;
	private final DateTimeFormat dateTimeFormat;

	private FileSystemActionHandler fileActionHandler;
	private FileItemDescriptionHandler descriptionHandler;

	private File file = File.Empty;
	private FileDetails details;

	private enum Details implements ResourceId {
		Accessed, Modified, Changed
	}

	public FileContextPresenter(FileItemContextComponent popup,
			SessionInfo session, FileDetailsProvider fileDetailsProvider,
			TextProvider textProvider) {
		this.popup = popup;
		this.fileDetailsProvider = fileDetailsProvider;
		this.textProvider = textProvider;
		this.dateTimeFormat = com.google.gwt.i18n.client.DateTimeFormat
				.getFormat(textProvider.getStrings().shortDateTimeFormat());

		initializeDetails();
	}

	private void initializeDetails() {
		List<ResourceId> order = (List<ResourceId>) Arrays.asList(
				(ResourceId) Details.Modified, (ResourceId) Details.Changed,
				(ResourceId) Details.Accessed);
		Map<ResourceId, String> headers = new HashMap();
		headers.put(Details.Accessed, textProvider.getStrings()
				.fileDetailsLabelLastAccessed());
		headers.put(Details.Changed, textProvider.getStrings()
				.fileDetailsLabelLastChanged());
		headers.put(Details.Modified, textProvider.getStrings()
				.fileDetailsLabelLastModified());

		this.popup.initializeDetailsSection(order, headers);
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
		popup.getName().setText(file.getName());
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
		this.popup.reset();
		this.details = details;
		this.updateDescription();

		if (details != null) {
			this.popup.setDetailValue(Details.Accessed, dateTimeFormat
					.format(details.getLastAccessed()));
			this.popup.setDetailValue(Details.Modified, dateTimeFormat
					.format(details.getLastModified()));
			this.popup.setDetailValue(Details.Changed, dateTimeFormat
					.format(details.getLastChanged()));
		}

		boolean writable = (details == null ? false : details
				.getFilePermission().canWrite());
		popup.updateButtons(writable);
	}

	private void updateDescription() {
		boolean descriptionDefined = isDescriptionDefined();
		String visibleDescription = descriptionDefined ? details
				.getDescription() : "";

		popup.setDescription(visibleDescription);
		popup.setDescriptionEditable(false, descriptionDefined);
	}

	private boolean isDescriptionDefined() {
		return (details != null && details.getDescription() != null);
	}

	protected void onStartEditDescription() {
		popup.setDescriptionEditable(true, isDescriptionDefined());
	}

	protected void onApplyDescription() {
		final String description = popup.getDescription().getText();
		if (!this.descriptionHandler.validateDescription(description))
			return;

		popup.setDescriptionEditable(false, isDescriptionDefined());

		this.descriptionHandler.setItemDescription(file, description,
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
	}

}
