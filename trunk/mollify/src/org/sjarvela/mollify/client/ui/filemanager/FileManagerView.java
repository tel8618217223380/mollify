/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filemanager;

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.DirectoryController;
import org.sjarvela.mollify.client.DirectoryProvider;
import org.sjarvela.mollify.client.FileHandler;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.UrlHandler;
import org.sjarvela.mollify.client.ui.dialog.ConfirmationDialog;
import org.sjarvela.mollify.client.ui.dialog.FileUploadDialog;
import org.sjarvela.mollify.client.ui.dialog.InfoDialog;
import org.sjarvela.mollify.client.ui.dialog.RenameDialog;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelector;
import org.sjarvela.mollify.client.ui.fileaction.FileActionPopup;
import org.sjarvela.mollify.client.ui.fileaction.FileActionProvider;
import org.sjarvela.mollify.client.ui.filelist.Column;
import org.sjarvela.mollify.client.ui.filelist.SimpleFileList;
import org.sjarvela.mollify.client.ui.filelist.SimpleFileListListener;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileManagerView extends Composite implements UrlHandler {
	private static final String FILEMANAGER_DOWNLOAD_PANEL_ID = "mollify-download-panel";
	private static final String FILEMANAGER_DOWNLOAD_FRAME_ID = "mollify-download-frame";

	private FileManagerModel model;
	private Localizator localizator;
	private DirectoryController directoryController;
	private FileHandler fileHandler;
	private FileActionProvider fileActionProvider;

	private DirectorySelector directorySelector;
	private SimpleFileList list;
	private FileActionPopup fileAction;

	public FileManagerView(FileManagerModel model, Localizator localizator) {
		this.model = model;
		this.localizator = localizator;

		initWidget(createControls());
		setStyleName(StyleConstants.MAIN_VIEW);
	}

	private Widget createControls() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(createHeader());
		panel.add(createFileList());
		panel.add(createDownloadFrame());
		return panel;
	}

	private Widget createFileList() {
		list = new SimpleFileList(model, localizator);
		return list;
	}

	private Widget createHeader() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setStyleName(StyleConstants.MAIN_VIEW_HEADER);

		Label leftPadding = new Label();
		leftPadding.setStyleName(StyleConstants.MAIN_VIEW_HEADER_PADDING);
		leftPadding.addStyleName(StyleConstants.LEFT);
		panel.add(leftPadding);

		panel.add(createToolButton(localizator.getStrings()
				.mainViewRefreshButtonTitle(),
				StyleConstants.MAIN_VIEW_TOOL_REFRESH, new ClickListener() {
					public void onClick(Widget sender) {
						directoryController.refresh();
					}
				}));

		directorySelector = new DirectorySelector(model, localizator);
		panel.add(directorySelector);

		panel.add(createToolButton(localizator.getStrings()
				.mainViewParentDirButtonTitle(),
				StyleConstants.MAIN_VIEW_TOOL_PARENT_DIR, new ClickListener() {
					public void onClick(Widget sender) {
						if (!model.getDirectoryModel().canAscend())
							return;
						directoryController.moveToParentDirectory();
					}
				}));

		panel.add(createToolButton(localizator.getStrings()
				.mainViewUploadFileButtonTitle(),
				StyleConstants.MAIN_VIEW_TOOL_UPLOAD_FILE, new ClickListener() {
					public void onClick(Widget sender) {
						new FileUploadDialog(model.getDirectoryModel()
								.getCurrentFolder(), localizator,
								fileActionProvider, fileHandler);
					}
				}));

		Label rightPadding = new Label();
		rightPadding.setStyleName(StyleConstants.MAIN_VIEW_HEADER_PADDING);
		rightPadding.addStyleName(StyleConstants.RIGHT);
		panel.add(rightPadding);

		return panel;
	}

	private Widget createToolButton(String title, String id,
			ClickListener listener) {
		Button button = new Button(title);
		button.addStyleName(StyleConstants.MAIN_VIEW_TOOL);
		button.getElement().setId(id);
		button.addClickListener(listener);
		return button;
	}

	private Widget createDownloadFrame() {
		SimplePanel downloadPanel = new SimplePanel();
		downloadPanel.getElement().setId(FILEMANAGER_DOWNLOAD_PANEL_ID);
		downloadPanel.getElement().setAttribute("style",
				"visibility:collapse; height: 0px;");

		Element downloadFrame = DOM.createIFrame();
		downloadFrame
				.setAttribute("style", "visibility:collapse; height: 0px;");
		downloadFrame.setId(FILEMANAGER_DOWNLOAD_FRAME_ID);

		downloadPanel.getElement().appendChild(downloadFrame);
		return downloadPanel;
	}

	void addFileListListener(SimpleFileListListener listener) {
		list.addListener(listener);
	}

	void setFileActionProvider(FileActionProvider fileActionProvider) {
		this.fileActionProvider = fileActionProvider;
		fileAction = new FileActionPopup(localizator, fileActionProvider);
	}

	void setDirectoryProvider(DirectoryProvider directoryProvider) {
		directorySelector.setDirectoryProvider(directoryProvider);
	}

	void setDirectoryController(DirectoryController directoryController) {
		this.directoryController = directoryController;
		directorySelector.setDirectoryController(directoryController);
	}

	void setFileHandler(FileHandler fileHandler) {
		this.fileHandler = fileHandler;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		directoryController.initialize();
	}

	public void refresh() {
		directorySelector.refresh();
		list.refresh();
	}

	public void clear() {
		list.removeAllRows();
	}

	public void showError(ServiceError error) {
		new InfoDialog(localizator, localizator.getStrings()
				.infoDialogErrorTitle(), error.getMessage(localizator),
				StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showInfo(String title, String text) {
		new InfoDialog(localizator, title, text,
				StyleConstants.INFO_DIALOG_TYPE_INFO);
	}

	public void showFileDeleteConfirmationDialog(File file,
			ConfirmationListener listener) {
		new ConfirmationDialog(localizator, localizator.getStrings()
				.deleteFileConfirmationDialogTitle(), localizator.getMessages()
				.confirmFileDeleteMessage(file.getName()),
				StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE_FILE, listener);
	}

	public void showFileActions(File file) {
		fileAction.initialize(file, list.getWidget(file, Column.NAME)
				.getElement());
		fileAction.show();
	}

	public void openDownloadUrl(String url) {
		setFrameUrl(FILEMANAGER_DOWNLOAD_FRAME_ID, url);
	}

	public void openUrlInNewWindow(String url) {
		Window.open(url, "_blank", "");
	}

	public void showRenameDialog(File file) {
		new RenameDialog(file, localizator, fileHandler);
	}

	public Localizator getLocalizator() {
		return localizator;
	}

	/* UTILITIES */

	private native void setFrameUrl(String id, String url) /*-{
		$doc.getElementById(id).src=url;
	}-*/;

}
