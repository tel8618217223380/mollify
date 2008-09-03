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

import org.sjarvela.mollify.client.DirectoryController;
import org.sjarvela.mollify.client.DirectoryProvider;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.RenameDialog;
import org.sjarvela.mollify.client.ui.UrlHandler;
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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileManagerView extends Composite implements UrlHandler {
	private FileManagerModel model;
	private Localizator localizator;
	private DirectoryController directoryController;

	private DirectorySelector directorySelector;
	private SimpleFileList list;
	private FileActionPopup fileAction;
	private Element downloadFrame;

	public FileManagerView(FileManagerModel model, Localizator localizator) {
		this.model = model;
		this.localizator = localizator;

		initWidget(createControls());
		setStyleName("filemanager-main");
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
		panel.setStyleName("header");

		Label leftPadding = new Label();
		leftPadding.setStyleName("header-padding");
		leftPadding.addStyleName("left");
		panel.add(leftPadding);

		panel.add(createToolButton(localizator.getStrings()
				.mainViewRefreshButtonTitle(), "refresh-button",
				new ClickListener() {
					public void onClick(Widget sender) {
						directoryController.refresh();
					}
				}));

		directorySelector = new DirectorySelector(model, localizator);
		panel.add(directorySelector);

		// HorizontalPanel buttons = new HorizontalPanel();
		// buttons.setStyleName("tools");

		panel.add(createToolButton(localizator.getStrings()
				.mainViewParentDirButtonTitle(), "parent-dir-button",
				new ClickListener() {
					public void onClick(Widget sender) {
						if (!model.getDirectoryModel().canAscend())
							return;
						directoryController.moveToParentDirectory();
					}
				}));

		// panel.add(buttons);
		Label rightPadding = new Label();
		rightPadding.setStyleName("header-padding");
		rightPadding.addStyleName("right");
		panel.add(rightPadding);

		return panel;
	}

	private Widget createToolButton(String title, String id,
			ClickListener listener) {
		Button button = new Button(title);
		button.addStyleName("tool");
		button.getElement().setId(id);
		button.addClickListener(listener);
		return button;
	}

	private Widget createDownloadFrame() {
		SimplePanel downloadPanel = new SimplePanel();
		downloadPanel.getElement().setId("filemanager-download-panel");
		downloadPanel.getElement().setAttribute("style",
				"visibility:collapse; height: 0px;");

		downloadFrame = DOM.createIFrame();
		downloadFrame
				.setAttribute("style", "visibility:collapse; height: 0px;");
		downloadFrame.setId("filemanager-download-frame");

		downloadPanel.getElement().appendChild(downloadFrame);
		return downloadPanel;
	}

	void addFileListListener(SimpleFileListListener listener) {
		list.addListener(listener);
	}

	void setFileActionProvider(FileActionProvider fileActionProvider) {
		fileAction = new FileActionPopup(localizator, fileActionProvider);
	}

	void setDirectoryProvider(DirectoryProvider directoryProvider) {
		directorySelector.setDirectoryProvider(directoryProvider);
	}

	void setDirectoryController(DirectoryController directoryController) {
		this.directoryController = directoryController;
		directorySelector.setDirectoryController(directoryController);
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

	public void showFileAction(File file) {
		fileAction.initialize(file, list.getWidget(file, Column.NAME)
				.getElement());
		fileAction.show();
	}

	public void openDownloadUrl(String url) {
		setFrameUrl(downloadFrame.getId(), url);
	}

	private native void setFrameUrl(String id, String url) /*-{
		$doc.getElementById(id).src=url;
	}-*/;

	public void openUrlInNewWindow(String url) {
		Window.open(url, "_blank", "");
	}

	public void showError(ServiceError error) {
		Window.alert(error.getMessage(localizator));
	}

	public void showRenameDialog(File file) {
		new RenameDialog(file, localizator).show();
	}
}
