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
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.session.FileSystemInfo;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
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
	private final TextProvider textProvider;
	private final FileUploadService fileUploadHandler;
	private final FileSystemInfo info;
	private final FileUploadListener listener;

	private FormPanel form;
	private Panel uploadersPanel;
	private Button uploadButton;
	private List<FileUpload> uploaders = new ArrayList();
	private DisclosurePanel uploadInfo;

	public FileUploadDialog(Directory directory, TextProvider textProvider,
			FileUploadService fileUploadHandler, FileSystemInfo info,
			FileUploadListener listener) {
		super(textProvider.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG);
		this.info = info;
		this.listener = listener;
		this.uploadId = fileUploadHandler.getFileUploadId();
		this.directory = directory;
		this.textProvider = textProvider;
		this.fileUploadHandler = fileUploadHandler;

		initialize();
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		uploadButton = createButton(textProvider.getStrings()
				.fileUploadDialogUploadButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		}, StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_UPLOAD);

		buttons.add(uploadButton);
		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						FileUploadDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_CONTENT);
		panel.add(createMessage());
		panel.add(createForm());
		panel.add(createUploaderButtons());
		panel.add(createInfoPanel());
		return panel;
	}

	private Widget createInfoPanel() {
		uploadInfo = new DisclosurePanel(textProvider.getStrings()
				.fileUploadDialogInfoTitle(), false);
		uploadInfo.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_INFO);
		uploadInfo.getHeader().getElement().getParentElement().setClassName(
				StyleConstants.FILE_UPLOAD_DIALOG_INFO_HEADER);

		HTML content = new HTML(textProvider.getMessages().uploadMaxSizeHtml(
				textProvider.getSizeText(info.getUploadMaxFileSize()),
				textProvider.getSizeText(info.getUploadMaxTotalSize())));
		content.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_INFO_CONTENT);

		uploadInfo.setContent(content);
		return uploadInfo;
	}

	private Widget createMessage() {
		Label message = new Label(textProvider.getStrings()
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

		uploaderButtons.add(createButton(textProvider.getStrings()
				.fileUploadDialogAddFileButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				onAddFile();
			}
		}, StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_ADD_FILE));

		uploaderButtons.add(createButton(textProvider.getStrings()
				.fileUploadDialogRemoveFileButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
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
