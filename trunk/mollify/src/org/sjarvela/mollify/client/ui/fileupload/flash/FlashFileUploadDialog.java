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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ProgressBar;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.swfupload.client.File;
import org.swfupload.client.SWFUpload.ButtonCursor;
import org.swfupload.client.SWFUpload.WindowMode;
import org.swfupload.client.UploadBuilder;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FlashFileUploadDialog extends CenteredDialog {
	private static Logger logger = Logger.getLogger(FlashFileUploadDialog.class
			.getName());

	enum Mode {
		Select, Upload
	};

	private static final String UPLOADER_ELEMENT_ID = "uploader";

	private final TextProvider textProvider;
	private final ActionListener actionListener;
	private final Map<String, FileComponent> fileItems = new HashMap();
	private final String uploaderStyle;

	private Panel header;
	private Panel uploadButtonContainer;
	private ScrollPanel fileScrollPanel;
	private Panel fileList;
	private HorizontalPanel buttons;
	private FileComponent activeItem;

	private Panel totalPanel;
	private Label totalProgress;
	private ProgressBar totalProgressBar;
	private Panel uploadButtonPanel;

	private String totalSizeText;

	private Label message;

	public enum Actions implements ResourceId {
		upload, cancel, cancelUpload, removeFile
	}

	public FlashFileUploadDialog(TextProvider textProvider,
			ActionListener actionListener, String uploaderStyle) {
		super(textProvider.getStrings().fileUploadDialogTitle(),
				StyleConstants.FILE_UPLOAD_DIALOG_FLASH);
		this.textProvider = textProvider;
		this.actionListener = actionListener;
		this.uploaderStyle = uploaderStyle;

		initialize();
		setMode(Mode.Select);
	}

	public void setVisualProperties(UploadBuilder builder) {
		builder.setButtonPlaceholderID(UPLOADER_ELEMENT_ID);
		builder.setButtonWidth(90);
		builder.setButtonHeight(20);
		builder.setButtonCursor(ButtonCursor.HAND);
		if (uploaderStyle != null && uploaderStyle.length() > 0)
			builder.setButtonTextStyle(uploaderStyle);
		builder.setWindowMode(WindowMode.TRANSPARENT);
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_CONTENT);
		panel.add(createHeader());
		panel.add(createFileList());
		panel.add(createTotalPanel());
		panel.add(createUploadButtons());
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

	private Widget createHeader() {
		header = new FlowPanel();
		header.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FLASH_HEADER);

		message = new Label(textProvider.getStrings().fileUploadDialogMessage());
		message.setStyleName(StyleConstants.FILE_UPLOAD_DIALOG_MESSAGE);
		header.add(message);

		uploadButtonContainer = new FlowPanel();
		Label label = new Label(textProvider.getStrings()
				.fileUploadDialogAddFilesButton());
		label.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FLASH_UPLOADER_LABEL);
		uploadButtonContainer.add(label);
		uploadButtonContainer
				.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FLASH_UPLOADER);
		uploadButtonContainer.addStyleDependentName(StyleConstants.HIDDEN);
		uploadButtonContainer.add(new HTML("<div id='" + UPLOADER_ELEMENT_ID
				+ "'/>"));
		header.add(uploadButtonContainer);
		return header;
	}

	public void showUploadButton() {
		uploadButtonContainer.removeStyleDependentName(StyleConstants.HIDDEN);
	}

	private Widget createTotalPanel() {
		totalPanel = new FlowPanel();
		totalPanel
				.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_TOTAL_PANEL);

		Label label = new Label(textProvider.getStrings()
				.fileUploadTotalProgressTitle());
		label.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_TOTAL_TITLE);
		totalPanel.add(label);

		Panel progressPanel = new FlowPanel();
		progressPanel
				.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_TOTAL_PROGRESS_PANEL);
		totalProgressBar = new ProgressBar(
				StyleConstants.FILE_UPLOAD_DIALOG_TOTAL_PROGRESS_BAR);
		totalProgressBar.setProgress(0d);
		progressPanel.add(totalProgressBar);
		totalPanel.add(progressPanel);

		totalProgress = new Label();
		totalProgress
				.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_TOTAL_PROGRESS);
		totalPanel.add(totalProgress);

		totalPanel.setVisible(false);
		return totalPanel;
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

	private Widget createUploadButtons() {
		uploadButtonPanel = new FlowPanel();
		uploadButtonPanel.add(createButton(textProvider.getStrings()
				.dialogCancelButton(), "cancel-upload",
				StyleConstants.FILE_UPLOAD_DIALOG_BUTTON, actionListener,
				Actions.cancelUpload));
		return uploadButtonPanel;
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
		logger.log(Level.SEVERE,
				"File queue error (" + errorCode + ") " + file.toString()
						+ ": " + message);
	}

	public void onUploadStarted(long totalSize) {
		totalSizeText = textProvider.getSizeText(totalSize);
		setMode(Mode.Upload);
	}

	public void cancel() {
		setMode(Mode.Select);
	}

	private void setMode(Mode mode) {
		if (Mode.Upload.equals(mode)) {
			header.addStyleDependentName(StyleConstants.UPLOAD);
			message.setText(textProvider.getStrings()
					.fileUploadProgressPleaseWait());
			fileList.addStyleDependentName(StyleConstants.UPLOAD);
			totalPanel.setVisible(true);
			buttons.setVisible(false);
			uploadButtonPanel.setVisible(true);
		} else {
			header.removeStyleDependentName(StyleConstants.UPLOAD);
			fileList.removeStyleDependentName(StyleConstants.UPLOAD);
			totalPanel.setVisible(false);
			uploadButtonPanel.setVisible(false);
		}
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

	public void cancelFile(File f, long totalSize, long totalProgress,
			double totalPercentage) {
		totalSizeText = textProvider.getSizeText(totalSize);
		fileItems.get(f.getId()).setCancelled();
		updateTotal(totalPercentage, totalProgress);
	}

	public void setProgress(File file, double percentage, long complete,
			double totalPercentage, long totalProgress) {
		activeItem.setProgress(percentage, complete);
		updateTotal(totalPercentage, totalProgress);
	}

	private void updateTotal(double totalPercentage, long totalProgress) {
		this.totalProgress.setText(textProvider.getSizeText(totalProgress)
				+ " / " + totalSizeText);
		this.totalProgressBar.setProgress(totalPercentage);
	}

	public void onFileUploadCompleted(File file) {
		fileItems.get(file.getId()).setFinished();
	}

}
