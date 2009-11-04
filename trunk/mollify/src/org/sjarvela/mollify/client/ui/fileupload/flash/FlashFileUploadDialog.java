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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.swfupload.client.File;
import org.swfupload.client.UploadBuilder;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FlashFileUploadDialog extends CenteredDialog {
	enum Mode {
		Select, Upload
	};

	private static final String UPLOADER_ELEMENT_ID = "uploader";

	private final TextProvider textProvider;
	private final ActionListener actionListener;
	private final Map<String, FileComponent> fileItems = new HashMap();

	private Panel selectHeader;
	private Label uploadHeader;
	private ScrollPanel fileScrollPanel;
	private Panel fileList;
	private HorizontalPanel buttons;
	private FileComponent activeItem;

	public enum Actions implements ResourceId {
		upload, cancel, removeFile
	}

	public FlashFileUploadDialog(TextProvider textProvider,
			ActionListener actionListener) {
		super(textProvider.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG_FLASH);
		this.textProvider = textProvider;
		this.actionListener = actionListener;

		initialize();
		setMode(Mode.Select);
	}

	public void setVisualProperties(UploadBuilder builder) {
		builder.setButtonText(textProvider.getStrings()
				.fileUploadDialogAddFileButton());
		builder.setButtonPlaceholderID(UPLOADER_ELEMENT_ID);
		builder.setButtonWidth(100);
		builder.setButtonHeight(20);
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_CONTENT);
		panel.add(createSelectModeHeader());
		panel.add(createUploadingMessage());
		panel.add(createFileList());
		return panel;
	}

	private Widget createFileList() {
		fileScrollPanel = new ScrollPanel();
		fileScrollPanel
				.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILES_PANEL);
		fileList = new FlowPanel();
		fileList.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILES);
		fileScrollPanel.add(fileList);
		return fileScrollPanel;
	}

	private Widget createSelectModeHeader() {
		selectHeader = new FlowPanel();
		selectHeader
				.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FLASH_HEADER);

		Label message = new Label(textProvider.getStrings()
				.fileUploadDialogMessage());
		message.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_MESSAGE);

		selectHeader.add(message);
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

		buttons.add(createButton(textProvider.getStrings()
				.fileUploadDialogUploadButton(),
				StyleConstants.FILE_UPLOAD_DIALOG_BUTTON_UPLOAD,
				StyleConstants.FILE_UPLOAD_DIALOG_BUTTON, actionListener,
				Actions.upload));
		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				StyleConstants.DIALOG_BUTTON_CANCEL,
				StyleConstants.FILE_UPLOAD_DIALOG_BUTTON, actionListener,
				Actions.cancel));

		return buttons;
	}

	private Widget createUploadingMessage() {
		uploadHeader = new Label(textProvider.getStrings()
				.fileUploadProgressPleaseWait());
		uploadHeader.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_MESSAGE);
		return uploadHeader;
	}

	public void addFile(File file) {
		FileComponent fileComponent = createFileComponent(file);
		fileItems.put(file.getId(), fileComponent);
		fileList.add(fileComponent);
	}

	public void removeFile(File file) {
		Widget item = fileItems.get(file.getId());
		fileItems.remove(file.getId());
		fileList.remove(item);
	}

	private FileComponent createFileComponent(final File file) {
		return new FileComponent(textProvider, file, actionListener,
				Actions.removeFile);
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

	public void cancel() {
		setMode(Mode.Select);
	}

	private void setMode(Mode mode) {
		if (Mode.Upload.equals(mode)) {
			selectHeader.addStyleDependentName(StyleConstants.HIDDEN);
			fileList.addStyleDependentName(StyleConstants.UPLOAD);
			uploadHeader.setVisible(true);
			// buttons.setVisible(false);
		} else {
			selectHeader.removeStyleDependentName(StyleConstants.HIDDEN);
			fileList.removeStyleDependentName(StyleConstants.UPLOAD);
			uploadHeader.setVisible(false);
		}

		for (FileComponent fc : fileItems.values())
			fc.setMode(mode);
	}

	public void onUploadEnded() {
		this.hide();
	}

	public void onActiveUploadFileChanged(File file) {
		if (activeItem != null)
			activeItem.setActive(false);

		FileComponent current = fileItems.get(file.getId());
		current.setActive(true);
		current.setProgress(0d, 0l);

		activeItem = current;
		fileScrollPanel.ensureVisible(activeItem);
	}

	public void setProgress(File file, double percentage, long complete) {
		activeItem.setProgress(percentage, complete);
	}

	public void onFileUploadCompleted(File file) {
		fileItems.get(file.getId()).setFinished();
	}

}
