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

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.file.FileAction;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileUploadDialog extends CenteredDialog implements FormHandler {
	private static final String UPLOAD_ID = "upload";

	private Directory directory;
	private Localizator localizator;
	private FileActionProvider fileActionProvider;
	private FileUploadHandler uploadHandler;

	private FormPanel form;
	private FileUpload uploader;

	public FileUploadDialog(Directory directory, Localizator localizator,
			FileActionProvider fileActionProvider,
			FileUploadHandler uploadHandler) {
		super(localizator.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG);
		this.directory = directory;
		this.localizator = localizator;
		this.fileActionProvider = fileActionProvider;
		this.uploadHandler = uploadHandler;

		initialize();
	}

	@Override
	Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(localizator.getStrings()
				.fileUploadDialogUploadButton(), new ClickListener() {

			public void onClick(Widget sender) {
				form.submit();
			}
		}, StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_UPLOAD));

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
		Label message = new Label(localizator.getStrings().fileUploadDialogMessage());
		message.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_MESSAGE);
		return message;
	}

	private Widget createForm() {
		form = new FormPanel();
		form.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_FORM);
		form.addFormHandler(this);
		form.setAction(fileActionProvider.getActionURL(directory,
				FileAction.UPLOAD));
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.add(createUploader());
		return form;
	}

	private Widget createUploader() {
		uploader = new FileUpload();
		uploader.addStyleName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_SELECTOR);
		uploader.setName(UPLOAD_ID);
		return uploader;
	}

	public void onSubmit(FormSubmitEvent event) {
		if (uploader.getFilename().length() < 1) {
			event.setCancelled(true);
		}
	}

	public void onSubmitComplete(FormSubmitCompleteEvent event) {
		this.hide();
		uploadHandler.getFileUploadResultHandler().handleResult(
				event.getResults());
	}
}
