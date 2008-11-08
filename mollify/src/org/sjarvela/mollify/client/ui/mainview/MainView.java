/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelector;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelectorFactory;
import org.sjarvela.mollify.client.ui.fileaction.FileDetailsPopup;
import org.sjarvela.mollify.client.ui.fileaction.FileDetailsPopupFactory;
import org.sjarvela.mollify.client.ui.filelist.Column;
import org.sjarvela.mollify.client.ui.filelist.SimpleFileList;
import org.sjarvela.mollify.client.ui.filelist.SimpleFileListListener;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite {
	private final MainViewModel model;
	private final Localizator localizator;

	private DirectorySelector directorySelector;
	private SimpleFileList list;
	private FileDetailsPopup fileDetails = null;
	private Button refreshButton;
	private Button parentDirButton;
	private Button uploadFileButton;
	private Button logoutButton;

	List<ViewListener> viewListeners = new ArrayList<ViewListener>();

	public MainView(MainViewModel model, Localizator localizator,
			DirectorySelectorFactory directorySelectorFactory,
			FileDetailsPopupFactory fileDetailsPopupFactory) {
		this.model = model;
		this.localizator = localizator;
		this.directorySelector = directorySelectorFactory.createSelector();
		this.fileDetails = fileDetailsPopupFactory.createPopup();

		initWidget(createControls());
		setStyleName(StyleConstants.MAIN_VIEW);
	}

	public void addViewListener(ViewListener listener) {
		viewListeners.add(listener);
	}

	public DirectorySelector getDirectorySelector() {
		return directorySelector;
	}

	private Widget createControls() {
		VerticalPanel content = new VerticalPanel();
		content.add(createHeader());
		content.add(createFileList());
		return content;
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

		createButtons();
		panel.add(refreshButton);
		panel.add(directorySelector);
		panel.add(parentDirButton);
		if (model.getSessionInfo().getSettings().isFileUploadEnabled())
			panel.add(uploadFileButton);

		Label rightPadding = new Label();
		rightPadding.setStyleName(StyleConstants.MAIN_VIEW_HEADER_PADDING);
		rightPadding.addStyleName(StyleConstants.RIGHT);
		panel.add(rightPadding);

		if (model.getSessionInfo().isAuthenticationRequired())
			panel.add(logoutButton);

		return panel;
	}

	private void createButtons() {
		refreshButton = createToolButton(localizator.getStrings()
				.mainViewRefreshButtonTitle(),
				StyleConstants.MAIN_VIEW_TOOL_REFRESH);

		parentDirButton = createToolButton(localizator.getStrings()
				.mainViewParentDirButtonTitle(),
				StyleConstants.MAIN_VIEW_TOOL_PARENT_DIR);

		uploadFileButton = createToolButton(localizator.getStrings()
				.mainViewUploadFileButtonTitle(),
				StyleConstants.MAIN_VIEW_TOOL_UPLOAD_FILE);

		logoutButton = createOptionButton(localizator.getStrings()
				.mainViewLogoutButtonTitle(),
				StyleConstants.MAIN_VIEW_HEADER_LOGOUT);
	}

	private Button createToolButton(String title, String id) {
		Button button = new Button(title);
		button.addStyleName(StyleConstants.MAIN_VIEW_TOOL);
		button.getElement().setId(id);
		return button;
	}

	private Button createOptionButton(String title, String id) {
		Button button = new Button(title);
		button.addStyleName(StyleConstants.MAIN_VIEW_HEADER_OPTION);
		button.getElement().setId(id);
		return button;
	}

	void addFileListListener(SimpleFileListListener listener) {
		list.addListener(listener);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		for (ViewListener listener : viewListeners)
			listener.onViewLoad();
	}

	public void refresh() {
		directorySelector.refresh();
		list.refresh();
	}

	public void clear() {
		list.removeAllRows();
	}

	public void showFileDetails(File file) {
		fileDetails.initialize(file, list.getWidget(file, Column.NAME)
				.getElement());
		fileDetails.show();
	}

	public Button getRefreshButton() {
		return refreshButton;
	}

	public Button getParentDirButton() {
		return parentDirButton;
	}

	public Button getUploadFileButton() {
		return uploadFileButton;
	}

	public Button getLogoutButton() {
		return logoutButton;
	}
}
