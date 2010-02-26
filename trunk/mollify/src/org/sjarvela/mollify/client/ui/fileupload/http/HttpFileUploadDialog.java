/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.http;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileSystemInfo;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.util.FileUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class HttpFileUploadDialog extends CenteredDialog implements
		SubmitHandler {
	private static final String UPLOADER_ID = "uploader-http[]";
	private static final String UPLOAD_ID_FIELD_NAME = "APC_UPLOAD_PROGRESS";

	private final String uploadId;
	private final Folder directory;
	private final TextProvider textProvider;
	private final FileUploadService service;
	private final FileSystemInfo info;
	private final FileUploadListener listener;
	private final List<String> allowedFileTypes;
	private final DialogManager dialogManager;

	private FormPanel form;
	private Panel uploadersPanel;
	private Button uploadButton;
	private List<FileUpload> uploaders = new ArrayList();
	private DisclosurePanel uploadInfo;

	public HttpFileUploadDialog(Folder directory, TextProvider textProvider,
			FileUploadService service, FileSystemInfo info,
			FileUploadListener listener, DialogManager dialogManager) {
		super(textProvider.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG);
		this.info = info;
		this.listener = listener;
		this.dialogManager = dialogManager;
		this.uploadId = service.getFileUploadId();
		this.directory = directory;
		this.textProvider = textProvider;
		this.service = service;
		this.allowedFileTypes = info.getAllowedFileUploadTypes();

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
						HttpFileUploadDialog.this.hide();
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
				.fileUploadDialogInfoTitle());
		uploadInfo.setOpen(false);
		uploadInfo.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_INFO);
		uploadInfo.getHeader().getElement().getParentElement().setClassName(
				StyleConstants.FILE_UPLOAD_DIALOG_INFO_HEADER);

		HTML content = new HTML(textProvider.getMessages().uploadMaxSizeHtml(
				textProvider.getSizeText((long) info.getUploadMaxFileSize()),
				textProvider.getSizeText((long) info.getUploadMaxTotalSize())));
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
		form.addSubmitHandler(this);
		form.addSubmitCompleteHandler(service
				.getUploadHandler(new ResultListener() {
					@Override
					public void onFail(ServiceError error) {
						HttpFileUploadDialog.this.hide();
						listener.onUploadFailed(error);
					}

					@Override
					public void onSuccess(Object result) {
						HttpFileUploadDialog.this.hide();
						listener.onUploadFinished();
					}
				}));
		form.setAction(this.service.getUploadUrl(directory));
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
		uploader.setName(UPLOADER_ID);
		uploaders.add(uploader);
		return uploader;
	}

	private boolean onStartUpload() {
		if (getLastUploader().getFilename().length() < 1)
			return false;
		if (!verifyFileTypes())
			return false;

		this.setVisible(false);
		return true;
	}

	private boolean verifyFileTypes() {
		if (allowedFileTypes.isEmpty())
			return true;

		for (FileUpload fu : uploaders)
			if (!verifyFileTypes(fu))
				return false;
		return true;
	}

	private boolean verifyFileTypes(FileUpload fu) {
		String extension = FileUtil.getExtension(fu.getFilename())
				.toLowerCase();
		if (!isAllowedExtension(extension)) {
			dialogManager.showInfo(textProvider.getStrings()
					.fileUploadDialogTitle(), textProvider.getMessages()
					.fileUploadDialogUnallowedFileType(extension));
			return false;
		}
		return true;
	}

	private boolean isAllowedExtension(String extension) {
		for (String ext : allowedFileTypes)
			if (ext.equalsIgnoreCase(extension))
				return true;
		return false;
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

	public void onSubmit(SubmitEvent event) {
		if (!onStartUpload()) {
			event.cancel();
			return;
		}

		listener.onUploadStarted(uploadId, getFileNames());
	}

}
