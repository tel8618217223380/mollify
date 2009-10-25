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
	private static final String UPLOADER_ELEMENT_ID = "uploader";

	private final TextProvider textProvider;
	private final FlashFileUploadHandler handler;

	private final Map<File, Widget> fileItems = new HashMap();
	private Panel fileList;

	public FlashFileUploadDialog(TextProvider textProvider,
			FlashFileUploadHandler handler) {
		super(textProvider.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG_FLASH);
		this.textProvider = textProvider;
		this.handler = handler;

		initialize();
		initializeUploader();
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
		panel.add(createMessage());
		panel.add(createUploaderElement());
		panel.add(createFileList());
		return panel;
	}

	private Widget createFileList() {
		fileList = new VerticalPanel();
		fileList.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILES);
		return fileList;
	}

	private Widget createUploaderElement() {
		Panel uploaderElement = new FlowPanel();
		uploaderElement.add(new HTML("<div class='"
				+ StyleConstants.FILE_UPLOAD_DIALOG_FLASH_UPLOADER + "' id='"
				+ UPLOADER_ELEMENT_ID + "'/>"));
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

	public void onFileAdded(File file) {
		Widget fileComponent = createFileComponent(file);
		fileItems.put(file, fileComponent);
		fileList.add(fileComponent);
	}

	protected void removeFile(File file) {
		handler.removeFile(file.getId());

		Widget item = fileItems.get(file);
		fileItems.remove(file);
		fileList.remove(item);
	}

	private Widget createFileComponent(final File file) {
		Panel item = new HorizontalPanel();
		item.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE);

		Label name = new Label(file.getName());
		name.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_NAME);
		item.add(name);

		Button button = new Button(textProvider.getStrings()
				.fileUploadDialogRemoveFileButton());
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeFile(file);
			}
		});
		item.add(button);

		return item;
	}

	public void onFileAddFailed(File file, int errorCode, String message) {
		GWT
				.log("ERROR (" + errorCode + ") " + file.toString() + ": "
						+ message, null);
		Log.error("File queue error (" + errorCode + ") " + file.toString()
				+ ": " + message);
	}

	public void onUploadEnded() {

	}

	public void onUploadStarted() {

	}

}
