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

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.swfupload.client.File;

import com.allen_sauer.gwt.log.client.Log;
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
		FileQueueListener, FlashProgressDisplayer {
	enum Mode {
		Select, Upload
	};

	private static final String UPLOADER_ELEMENT_ID = "uploader";

	private final TextProvider textProvider;
	private final FlashFileUploadHandler handler;

	private final Map<File, FileComponent> fileItems = new HashMap();
	private Mode mode;

	private Panel selectHeader;
	private Label uploadHeader;
	private Panel fileList;
	private HorizontalPanel buttons;

	private FileComponent activeItem;

	public FlashFileUploadDialog(TextProvider textProvider,
			FlashFileUploadHandler handler) {
		super(textProvider.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG_FLASH);
		this.textProvider = textProvider;
		this.handler = handler;

		initialize();
		initializeUploader();

		setMode(Mode.Select);
	}

	private void initializeUploader() {
		handler.setProgressDisplayer(this);

		handler.setButtonProperties(UPLOADER_ELEMENT_ID, 100, 20, textProvider
				.getStrings().fileUploadDialogAddFileButton());
		handler.setFileQueueListener(this);
		handler.initialize();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_CONTENT);
		panel.add(createUploaderElement());
		panel.add(createUploadingMessage());
		panel.add(createFileList());
		return panel;
	}

	private Widget createFileList() {
		fileList = new VerticalPanel();
		fileList.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILES);
		return fileList;
	}

	private Widget createUploaderElement() {
		selectHeader = new FlowPanel();
		selectHeader.add(createMessage());
		selectHeader.add(new HTML("<div class='"
				+ StyleConstants.FILE_UPLOAD_DIALOG_FLASH_UPLOADER + "' id='"
				+ UPLOADER_ELEMENT_ID + "'/>"));
		return selectHeader;
	}

	@Override
	protected Widget createButtons() {
		buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		Button uploadButton = createButton(textProvider.getStrings()
				.fileUploadDialogUploadButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				handler.startUpload();
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

	private Widget createUploadingMessage() {
		uploadHeader = new Label(textProvider.getStrings()
				.fileUploadProgressPleaseWait());
		uploadHeader.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_MESSAGE);
		return uploadHeader;
	}

	public void onFileAdded(File file) {
		FileComponent fileComponent = createFileComponent(file);
		fileItems.put(file, fileComponent);
		fileList.add(fileComponent);
	}

	protected void removeFile(File file) {
		handler.removeFile(file.getId());

		Widget item = fileItems.get(file);
		fileItems.remove(file);
		fileList.remove(item);
	}

	private FileComponent createFileComponent(final File file) {
		FileComponent c = new FileComponent(textProvider, file,
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						removeFile(file);
					}
				});
		return c;
	}

	public void onFileAddFailed(File file, int errorCode, String message) {
		GWT
				.log("ERROR (" + errorCode + ") " + file.toString() + ": "
						+ message, null);
		Log.error("File queue error (" + errorCode + ") " + file.toString()
				+ ": " + message);
	}

	public void onUploadStarted() {
		setMode(Mode.Upload);
	}

	private void setMode(Mode mode) {
		this.mode = mode;

		if (Mode.Upload.equals(mode)) {
			selectHeader.setHeight("0px");
			uploadHeader.setVisible(true);
			buttons.setVisible(false);
		} else {
			uploadHeader.setVisible(false);
		}

		for (FileComponent fc : fileItems.values())
			fc.setMode(mode);
	}

	public void onUploadEnded() {
		this.hide();
	}

	public void onUploadError() {
		this.hide();
	}

	public void onActiveUploadFileChanged(File file) {
		if (activeItem != null)
			activeItem.setActive(false);

		FileComponent current = fileItems.get(file);
		current.setActive(true);
		current.setProgress(0d);

		activeItem = current;
	}

	public void setProgress(File file, double percentage) {
		activeItem.setProgress(percentage);
	}

}
