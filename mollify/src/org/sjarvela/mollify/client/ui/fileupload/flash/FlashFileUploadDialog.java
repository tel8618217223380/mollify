/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.flash;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.session.file.FileSystemInfo;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.swfupload.client.File;
import org.swfupload.client.SWFUpload;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.SWFUpload.ButtonAction;
import org.swfupload.client.event.FileDialogCompleteHandler;
import org.swfupload.client.event.FileQueueErrorHandler;
import org.swfupload.client.event.FileQueuedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FlashFileUploadDialog extends CenteredDialog implements
		FileDialogCompleteHandler, FileQueuedHandler, FileQueueErrorHandler {
	private static final String UPLOADER_ELEMENT_ID = "uploader";

	private final TextProvider textProvider;
	private final FileUploadService service;
	private final FlashFileUploadListener listener;

	private final List<File> files = new ArrayList();
	private final SWFUpload uploader;

	public FlashFileUploadDialog(Directory directory,
			TextProvider textProvider, FileUploadService fileUploadHandler,
			FileSystemInfo info, FlashFileUploadListener listener) {
		super(textProvider.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG);
		this.textProvider = textProvider;
		this.service = fileUploadHandler;
		this.listener = listener;

		initialize();

		uploader = createUploader(directory);
	}

	private SWFUpload createUploader(Directory directory) {
		UploadBuilder builder = new UploadBuilder();
		builder.setUploadURL(service.getUploadUrl(directory));

		builder.setButtonPlaceholderID(UPLOADER_ELEMENT_ID);
		builder.setButtonHeight(20);
		builder.setButtonWidth(100);
		builder.setButtonText(textProvider.getStrings()
				.fileUploadDialogAddFileButton());
		builder.setButtonAction(ButtonAction.SELECT_FILES);

		builder.setFileDialogCompleteHandler(this);
		builder.setFileQueuedHandler(this);
		builder.setFileQueueErrorHandler(this);

		builder.setUploadStartHandler(listener);
		builder.setUploadErrorHandler(listener);
		builder.setUploadCompleteHandler(listener);
		builder.setUploadProgressHandler(listener);
		builder.setUploadSuccessHandler(listener);

		return builder.build();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_CONTENT);
		panel.add(createMessage());
		panel.add(createUploaderElement());
		// panel.add(createUploaderButtons());
		// panel.add(createInfoPanel());
		return panel;
	}

	private Widget createUploaderElement() {
		Panel uploaderElement = new FlowPanel();
		uploaderElement
				.add(new HTML("<div id='" + UPLOADER_ELEMENT_ID + "'/>"));
		return uploaderElement;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		Button uploadButton = createButton(textProvider.getStrings()
				.fileUploadDialogUploadButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				uploader.startUpload();
			}
		}, StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_UPLOAD);

		buttons.add(uploadButton);
		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						FlashFileUploadDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	private Widget createMessage() {
		Label message = new Label(textProvider.getStrings()
				.fileUploadDialogMessage());
		message.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_MESSAGE);
		return message;
	}

	public void onFileDialogComplete(FileDialogCompleteEvent e) {

	}

	public void onFileQueued(FileQueuedEvent e) {
		files.add(e.getFile());
		GWT.log(e.getFile().toString(), null);
	}

	public void onFileQueueError(FileQueueErrorEvent e) {
		GWT.log("ERROR (" + e.getErrorCode() + ") " + e.getFile().toString()
				+ ": " + e.getMessage(), null);
	}
}
