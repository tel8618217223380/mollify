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

import org.sjarvela.mollify.client.TextProvider;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.ActionId;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.MollifyButton;
import org.sjarvela.mollify.client.ui.DropdownButton;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.contextpopup.directorycontext.DirectoryContextPopup;
import org.sjarvela.mollify.client.ui.contextpopup.directorycontext.DirectoryContextPopupFactory;
import org.sjarvela.mollify.client.ui.contextpopup.filecontext.FileContextPopup;
import org.sjarvela.mollify.client.ui.contextpopup.filecontext.FileContextPopupFactory;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelector;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelectorFactory;
import org.sjarvela.mollify.client.ui.filelist.Column;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.filelist.FileListListener;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite {
	private final MainViewModel model;
	private final Localizator localizator;

	private DirectorySelector directorySelector;
	private FileList list;
	private FileContextPopup fileContext = null;
	private DirectoryContextPopup dirContext = null;

	private DropdownButton addButton;
	private MollifyButton refreshButton;
	private MollifyButton parentDirButton;
	private MollifyButton logoutButton;

	List<ViewListener> viewListeners = new ArrayList<ViewListener>();
	private final ActionListener actionListener;
	private final TextProvider textProvider;

	public enum Action implements ActionId {
		addFile, addDirectory, refresh, parentDir, logout;
	};

	public MainView(MainViewModel model, TextProvider textProvider,
			Localizator localizator, ActionListener actionListener,
			DirectorySelectorFactory directorySelectorFactory,
			FileContextPopupFactory fileContextPopupFactory,
			DirectoryContextPopupFactory directoryContextPopupFactory) {
		this.model = model;
		this.textProvider = textProvider;
		this.localizator = localizator;
		this.actionListener = actionListener;
		this.directorySelector = directorySelectorFactory.createSelector();
		this.fileContext = fileContextPopupFactory.createPopup();
		this.dirContext = directoryContextPopupFactory.createPopup();

		initWidget(createControls());
		setStyleName(StyleConstants.MAIN_VIEW);
	}

	public void addViewListener(ViewListener listener) {
		viewListeners.add(listener);
	}

	public DirectorySelector getDirectorySelector() {
		return directorySelector;
	}

	public FileList getList() {
		return list;
	}

	private Widget createControls() {
		VerticalPanel content = new VerticalPanel();
		content.add(createHeader());
		content.add(createFileList());
		return content;
	}

	private Widget createFileList() {
		list = new FileList(textProvider, localizator);
		return list;
	}

	private Widget createHeader() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setStyleName(StyleConstants.MAIN_VIEW_HEADER);

		Label leftPadding = new Label();
		leftPadding.setStyleName(StyleConstants.MAIN_VIEW_HEADER_PADDING_LEFT);
		panel.add(leftPadding);

		createButtons();

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setStyleName(StyleConstants.MAIN_VIEW_HEADER_BUTTONS);

		if (addButton != null)
			buttonPanel.add(addButton);
		buttonPanel.add(refreshButton);
		buttonPanel.add(directorySelector);
		buttonPanel.add(parentDirButton);
		panel.add(buttonPanel);

		Label rightPadding = new Label();
		rightPadding
				.setStyleName(StyleConstants.MAIN_VIEW_HEADER_PADDING_RIGHT);
		panel.add(rightPadding);

		if (model.getSessionInfo().isAuthenticationRequired())
			panel.add(logoutButton);

		return panel;
	}

	private void createButtons() {
		refreshButton = new MollifyButton(localizator.getStrings()
				.mainViewRefreshButtonTitle(),
				StyleConstants.MAIN_VIEW_HEADER_BUTTON_REFRESH,
				StyleConstants.MAIN_VIEW_HEADER_BUTTON);
		refreshButton.setAction(actionListener, Action.refresh);

		parentDirButton = new MollifyButton(localizator.getStrings()
				.mainViewParentDirButtonTitle(),
				StyleConstants.MAIN_VIEW_HEADER_BUTTON_PARENT_DIR,
				StyleConstants.MAIN_VIEW_HEADER_BUTTON);
		parentDirButton.setAction(actionListener, Action.parentDir);

		logoutButton = new MollifyButton(localizator.getStrings()
				.mainViewLogoutButtonTitle(),
				StyleConstants.MAIN_VIEW_HEADER_LOGOUT,
				StyleConstants.MAIN_VIEW_HEADER_OPTION);
		logoutButton.setAction(actionListener, Action.logout);

		if ((model.getSessionInfo().getSettings().isFileUploadEnabled() || model
				.getSessionInfo().getSettings().isFolderActionsEnabled())
				&& model.getSessionInfo().getPermissionMode()
						.hasWritePermission()) {
			addButton = new DropdownButton(actionListener, localizator
					.getStrings().mainViewAddButtonTitle(),
					StyleConstants.MAIN_VIEW_HEADER_BUTTON_ADD);
			if (model.getSessionInfo().getSettings().isFileUploadEnabled())
				addButton.addAction(Action.addFile, localizator.getStrings()
						.mainViewAddFileMenuItem());
			if (model.getSessionInfo().getSettings().isFolderActionsEnabled())
				addButton.addAction(Action.addDirectory, localizator
						.getStrings().mainViewAddDirectoryMenuItem());
		}
	}

	void addFileListListener(FileListListener listener) {
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
		model.clear();
	}

	public void showFileContext(File file) {
		fileContext
				.update(file, list.getWidget(file, Column.NAME).getElement());
		fileContext.show();
	}

	public void showDirectoryContext(Directory directory) {
		dirContext.update(directory, list.getWidget(directory, Column.NAME)
				.getElement());
		dirContext.show();
	}

	public MollifyButton getRefreshButton() {
		return refreshButton;
	}

	public MollifyButton getParentDirButton() {
		return parentDirButton;
	}

	public MollifyButton getLogoutButton() {
		return logoutButton;
	}
}
