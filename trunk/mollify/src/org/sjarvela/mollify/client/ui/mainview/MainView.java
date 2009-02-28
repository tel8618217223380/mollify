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

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.ActionId;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelector;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelectorFactory;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.popup.directorycontext.DirectoryContextPopup;
import org.sjarvela.mollify.client.ui.popup.directorycontext.DirectoryContextPopupFactory;
import org.sjarvela.mollify.client.ui.popup.filecontext.FileContextPopup;
import org.sjarvela.mollify.client.ui.popup.filecontext.FileContextPopupFactory;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite {
	private final MainViewModel model;
	private final TextProvider textProvider;
	private final ActionListener actionListener;

	private DirectorySelector directorySelector;
	private FileList list;
	private FileContextPopup fileContext = null;
	private DirectoryContextPopup dirContext = null;

	private DropdownButton addButton;
	private ActionButton refreshButton;
	private ActionButton parentDirButton;
	private Label username;
	private ActionButton logoutButton;

	List<ViewListener> viewListeners = new ArrayList<ViewListener>();

	public enum Action implements ActionId {
		addFile, addDirectory, refresh, parentDir, logout;
	};

	public MainView(MainViewModel model, TextProvider textProvider,
			ActionListener actionListener,
			DirectorySelectorFactory directorySelectorFactory,
			FileContextPopupFactory fileContextPopupFactory,
			DirectoryContextPopupFactory directoryContextPopupFactory) {
		this.model = model;
		this.textProvider = textProvider;
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

	public void setFileContextHandler(FileSystemActionHandler actionHandler) {
		fileContext.setFileActionHandler(actionHandler);
	}

	public void setDirectoryContextHandler(FileSystemActionHandler actionHandler) {
		dirContext.setDirectoryActionHandler(actionHandler);
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
		list = new FileList(textProvider);
		return list;
	}

	private Widget createHeader() {
		createButtons();

		Panel header = new HorizontalPanel();
		header.setStyleName(StyleConstants.MAIN_VIEW_HEADER);

		Panel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName(StyleConstants.MAIN_VIEW_HEADER_BUTTONS);

		if (addButton != null)
			buttonPanel.add(addButton);
		buttonPanel.add(refreshButton);
		buttonPanel.add(parentDirButton);
		buttonPanel.add(directorySelector);
		header.add(buttonPanel);

		if (model.getSessionInfo().isAuthenticationRequired()) {
			Panel loggedInPanel = new FlowPanel();
			loggedInPanel
					.setStyleName(StyleConstants.MAIN_VIEW_HEADER_LOGGED_IN);
			loggedInPanel.add(createUserName());
			loggedInPanel.add(logoutButton);
			header.add(loggedInPanel);
		}

		return header;
	}

	private Widget createUserName() {
		username = new Label();
		username.setStyleName(StyleConstants.MAIN_VIEW_HEADER_USERNAME);
		return username;
	}

	private void createButtons() {
		refreshButton = new ActionButton(textProvider.getStrings()
				.mainViewRefreshButtonTitle(),
				StyleConstants.MAIN_VIEW_HEADER_BUTTON_REFRESH,
				StyleConstants.MAIN_VIEW_HEADER_BUTTON);
		refreshButton.setAction(actionListener, Action.refresh);

		parentDirButton = new ActionButton(textProvider.getStrings()
				.mainViewParentDirButtonTitle(),
				StyleConstants.MAIN_VIEW_HEADER_BUTTON_PARENT_DIR,
				StyleConstants.MAIN_VIEW_HEADER_BUTTON);
		parentDirButton.setAction(actionListener, Action.parentDir);

		logoutButton = new ActionButton(textProvider.getStrings()
				.mainViewLogoutButtonTitle(),
				StyleConstants.MAIN_VIEW_HEADER_LOGOUT,
				StyleConstants.MAIN_VIEW_HEADER_OPTION);
		logoutButton.setAction(actionListener, Action.logout);

		if ((model.getSessionInfo().getSettings().isFileUploadEnabled() || model
				.getSessionInfo().getSettings().isFolderActionsEnabled())
				&& model.getSessionInfo().getPermissionMode()
						.hasWritePermission()) {
			addButton = new DropdownButton(actionListener, textProvider
					.getStrings().mainViewAddButtonTitle(),
					StyleConstants.MAIN_VIEW_HEADER_BUTTON_ADD);
			if (model.getSessionInfo().getSettings().isFileUploadEnabled())
				addButton.addAction(Action.addFile, textProvider.getStrings()
						.mainViewAddFileMenuItem());
			if (model.getSessionInfo().getSettings().isFolderActionsEnabled())
				addButton.addAction(Action.addDirectory, textProvider
						.getStrings().mainViewAddDirectoryMenuItem());
		}
	}

	void addFileListListener(GridListener listener) {
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
		fileContext.update(file, list.getWidget(file, FileList.COLUMN_NAME)
				.getElement());
		fileContext.show();
	}

	public void showDirectoryContext(Directory directory) {
		dirContext.update(directory, list.getWidget(directory,
				FileList.COLUMN_NAME).getElement());
		dirContext.show();
	}

	public ActionButton getRefreshButton() {
		return refreshButton;
	}

	public ActionButton getParentDirButton() {
		return parentDirButton;
	}

	public Label getUsername() {
		return username;
	}

	public ActionButton getLogoutButton() {
		return logoutButton;
	}
}
