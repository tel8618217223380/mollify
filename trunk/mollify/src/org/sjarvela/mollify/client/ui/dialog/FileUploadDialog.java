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

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.file.FileActionUrlProvider;
import org.sjarvela.mollify.client.file.FileSystemAction;
import org.sjarvela.mollify.client.file.FileUploadController;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileUploadDialog extends CenteredDialog implements
		FileUploadController {
	private static final String UPLOADER_NAME = "upload[]";
	private static final String UPLOAD_ID_FIELD_NAME = "APC_UPLOAD_PROGRESS";

	private final String uploadId;
	private final Directory directory;
	private final Localizator localizator;
	private final FileActionUrlProvider actionUrlProvider;
	private final FileUploadHandler fileUploadHandler;
	private Button uploadButton;

	private FormPanel form;
	private Panel uploadersPanel;
	private List<FileUpload> uploaders = new ArrayList();

	public FileUploadDialog(Directory directory, Localizator localizator,
			FileActionUrlProvider actionUrlProvider,
			FileUploadHandler fileUploadHandler) {
		super(localizator.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG);
		this.uploadId = fileUploadHandler.getFileUploadId();
		this.directory = directory;
		this.localizator = localizator;
		this.actionUrlProvider = actionUrlProvider;
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
		form.addFormHandler(fileUploadHandler.getUploadFormHandler(this,
				uploadId));
		form.setAction(actionUrlProvider.getActionUrl(directory,
				FileSystemAction.upload));
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);
		panel.add(new Hidden(UPLOAD_ID_FIELD_NAME, uploadId));

		uploadersPanel = new VerticalPanel();
		uploadersPanel.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_FILES);
		uploadersPanel.add(createUploader());

		panel.add(uploadersPanel);
		panel.add(createButton(localizator.getStrings()
				.fileUploadDialogAddFileButton(), new ClickListener() {
			public void onClick(Widget sender) {
				onAddFile();
			}
		}, StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_ADD_FILE));

		return form;
	}

	protected void onAddFile() {
		if (getLastUploader().getFilename().length() < 1)
			return;
		uploadersPanel.add(createUploader());
	}

	private Widget createUploader() {
		FileUpload uploader = new FileUpload();
		uploader.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_SELECTOR);
		uploader.setName(UPLOADER_NAME);
		uploaders.add(uploader);
		return uploader;
	}

	public boolean onStartUpload() {
		if (getLastUploader().getFilename().length() < 1)
			return false;
		this.setVisible(false);
		return true;
	}

	private FileUpload getLastUploader() {
		return uploaders.get(uploaders.size() - 1);
	}

	public List<String> getFileNames() {
		List<String> result = new ArrayList();
		for (FileUpload uploader : uploaders)
			result.add(uploader.getFilename());
		return result;
	}

	public void onUploadFinished() {
		this.hide();
	}
}
