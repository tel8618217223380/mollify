/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileUploadDialog extends CenteredDialog implements FormHandler {
	private static final String UPLOADER_NAME = "upload[]";
	private static final String UPLOAD_ID_FIELD_NAME = "APC_UPLOAD_PROGRESS";

	private final String uploadId;
	private final Directory directory;
	private final Localizator localizator;
	private final FileUploadService fileUploadHandler;
	private final FileUploadListener listener;

	private FormPanel form;
	private Panel uploadersPanel;
	private Button uploadButton;
	private List<FileUpload> uploaders = new ArrayList();

	public FileUploadDialog(Directory directory, Localizator localizator,
			FileUploadService fileUploadHandler, FileUploadListener listener) {
		super(localizator.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG);
		this.listener = listener;
		this.uploadId = fileUploadHandler.getFileUploadId();
		this.directory = directory;
		this.localizator = localizator;
		this.fileUploadHandler = fileUploadHandler;

		initialize();
	}

	@Override
	Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		uploadButton = createButton(localizator.getStrings()
				.fileUploadDialogUploadButton(), new ClickListener() {
			public void onClick(Widget sender) {
				form.submit();
			}
		}, StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_UPLOAD);

		buttons.add(uploadButton);
		buttons.add(createButton(localizator.getStrings().dialogCancelButton(),
				new ClickListener() {
					public void onClick(Widget sender) {
						FileUploadDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	@Override
	Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_CONTENT);
		panel.add(createMessage());
		panel.add(createForm());
		panel.add(createUploaderButtons());
		return panel;
	}

	private Widget createMessage() {
		Label message = new Label(localizator.getStrings()
				.fileUploadDialogMessage());
		message.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_MESSAGE);
		return message;
	}

	private Widget createForm() {
		form = new FormPanel();
		form.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_FORM);
		form.addFormHandler(this);
		form.setAction(this.fileUploadHandler.getUploadUrl(directory));
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);
		panel.add(new Hidden(UPLOAD_ID_FIELD_NAME, uploadId));

		uploadersPanel = new VerticalPanel();
		uploadersPanel.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_FILES);
		uploadersPanel.add(createUploader());

		panel.add(uploadersPanel);

		return form;
	}

	private Panel createUploaderButtons() {
		Panel uploaderButtons = new HorizontalPanel();
		uploaderButtons
				.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_UPLOADERS_BUTTONS);

		uploaderButtons.add(createButton(localizator.getStrings()
				.fileUploadDialogAddFileButton(), new ClickListener() {
			public void onClick(Widget sender) {
				onAddFile();
			}
		}, StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_ADD_FILE));

		uploaderButtons.add(createButton(localizator.getStrings()
				.fileUploadDialogRemoveFileButton(), new ClickListener() {
			public void onClick(Widget sender) {
				onRemoveFile();
			}
		}, StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_REMOVE_FILE));

		return uploaderButtons;
	}

	protected void onAddFile() {
		if (getLastUploader().getFilename().length() < 1)
			return;
		uploadersPanel.add(createUploader());
	}

	protected void onRemoveFile() {
		if (uploaders.size() < 2)
			return;

		FileUpload lastUploader = getLastUploader();
		uploaders.remove(lastUploader);
		uploadersPanel.remove(lastUploader);
	}

	private Widget createUploader() {
		FileUpload uploader = new FileUpload();
		uploader.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_SELECTOR);
		uploader.setName(UPLOADER_NAME);
		uploaders.add(uploader);
		return uploader;
	}

	private boolean onStartUpload() {
		if (getLastUploader().getFilename().length() < 1)
			return false;
		this.setVisible(false);
		return true;
	}

	private FileUpload getLastUploader() {
		return uploaders.get(uploaders.size() - 1);
	}

	private List<String> getFileNames() {
		List<String> result = new ArrayList();
		for (FileUpload uploader : uploaders)
			result.add(uploader.getFilename());
		return result;
	}

	public void onSubmit(FormSubmitEvent event) {
		if (!onStartUpload()) {
			event.setCancelled(true);
			return;
		}

		listener.onUploadStarted(uploadId, getFileNames());
	}

	public void onSubmitComplete(FormSubmitCompleteEvent event) {
		this.hide();
		fileUploadHandler.handleResult(event.getResults(), listener);
	}
}
