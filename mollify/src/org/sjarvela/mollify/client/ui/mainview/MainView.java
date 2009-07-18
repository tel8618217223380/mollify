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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;
import org.sjarvela.mollify.client.ui.common.popup.DropdownPopup;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelector;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelectorFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextPopupHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.directorycontext.DirectoryContextPopup;
import org.sjarvela.mollify.client.ui.fileitemcontext.directorycontext.DirectoryContextPopupFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.filecontext.FileContextPopup;
import org.sjarvela.mollify.client.ui.fileitemcontext.filecontext.FileContextPopupFactory;
import org.sjarvela.mollify.client.ui.filelist.FileList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite implements PopupPositioner {
	private final MainViewModel model;
	private final TextProvider textProvider;
	private final ActionListener actionListener;

	private final Panel buttonPanel;
	private final DirectorySelector directorySelector;
	private final FileList list;

	private final FileContextPopup fileContextPopup;
	private final ContextPopupHandler<File> fileContextHandler;

	private final DirectoryContextPopup directoryContextPopup;
	private final ContextPopupHandler<Directory> directoryContextHandler;

	private DropdownButton addButton;
	private ActionButton refreshButton;
	private DropdownButton username;

	List<ViewListener> viewListeners = new ArrayList<ViewListener>();

	public enum Action implements ResourceId {
		addFile, addDirectory, refresh, logout, changePassword, configure, editItemPermissions;
	};

	public MainView(MainViewModel model, TextProvider textProvider,
			ActionListener actionListener,
			DirectorySelectorFactory directorySelectorFactory,
			FileContextPopupFactory fileContextPopupFactory,
			DirectoryContextPopupFactory directoryContextPopupFactory) {
		this.model = model;
		this.textProvider = textProvider;
		this.actionListener = actionListener;

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName(StyleConstants.MAIN_VIEW_HEADER_BUTTONS);

		this.directorySelector = directorySelectorFactory.createSelector();
		this.list = new FileList(textProvider);

		this.fileContextPopup = fileContextPopupFactory.createPopup();
		this.fileContextPopup.setPopupPositioner(this);
		this.fileContextHandler = new ContextPopupHandler<File>(
				fileContextPopup);

		this.directoryContextPopup = directoryContextPopupFactory.createPopup();
		this.fileContextPopup.setPopupPositioner(this);
		this.directoryContextHandler = new ContextPopupHandler<Directory>(
				directoryContextPopup);

		initWidget(createControls());
		setStyleName(StyleConstants.MAIN_VIEW);
	}

	public void addViewListener(ViewListener listener) {
		viewListeners.add(listener);
	}

	public void setFileContextHandler(FileSystemActionHandler actionHandler) {
		fileContextPopup.setFileActionHandler(actionHandler);
	}

	public void setFileItemDescriptionHandler(
			FileItemDescriptionHandler descriptionHandler) {
		fileContextPopup.setFileItemDescriptionHandler(descriptionHandler);
	}

	public void setDirectoryContextHandler(FileSystemActionHandler actionHandler) {
		directoryContextPopup.setDirectoryActionHandler(actionHandler);
	}

	public FileList getList() {
		return list;
	}

	private Widget createControls() {
		VerticalPanel content = new VerticalPanel();
		content.add(createHeader());
		content.add(list);
		return content;
	}

	private Widget createHeader() {
		createButtons();

		Panel header = new HorizontalPanel();
		header.setStyleName(StyleConstants.MAIN_VIEW_HEADER);

		if (addButton != null)
			buttonPanel.add(addButton);
		buttonPanel.add(refreshButton);
		buttonPanel.add(directorySelector);
		header.add(buttonPanel);

		if (model.getSession().isAuthenticationRequired()) {
			Panel loggedInPanel = new FlowPanel();
			loggedInPanel
					.setStyleName(StyleConstants.MAIN_VIEW_HEADER_LOGGED_IN);
			loggedInPanel.add(createUserName());
			header.add(loggedInPanel);
		}

		return header;
	}

	private Widget createUserName() {
		username = new DropdownButton(actionListener, "", "username", null,
				new PopupPositioner() {
					public void setPositionOnShow(DropdownPopup popup,
							Widget parent, int offsetWidth, int offsetHeight) {
						int x = Math.min(parent.getAbsoluteLeft(),
								MainView.this.getOffsetWidth() - offsetWidth);
						popup.setPopupPosition(x, parent.getAbsoluteTop());
					}
				});
		username.setStyleName(StyleConstants.MAIN_VIEW_HEADER_USERNAME);

		if (model.getSession().getDefaultPermissionMode().isAdmin()
				&& model.getSession().getFeatures().permissionUpdate()) {
			username.addAction(Action.editItemPermissions, textProvider
					.getStrings().mainViewEditPermissionsTitle());

			if (model.getSession().getFeatures().configurationUpdate()) {
				username.addAction(Action.configure, textProvider.getStrings()
						.mainViewConfigurationTitle());
			}
			username.addSeparator();
		}

		if (model.getSession().getFeatures().configurationUpdate()) {
			username.addAction(Action.changePassword, textProvider.getStrings()
					.mainViewChangePasswordTitle());
			username.addSeparator();
		}

		username.addAction(Action.logout, textProvider.getStrings()
				.mainViewLogoutButtonTitle());
		return username;
	}

	private void createButtons() {
		refreshButton = new ActionButton(textProvider.getStrings()
				.mainViewRefreshButtonTitle(),
				StyleConstants.MAIN_VIEW_HEADER_BUTTON_REFRESH,
				StyleConstants.MAIN_VIEW_HEADER_BUTTON);
		refreshButton.setAction(actionListener, Action.refresh);

		if ((model.getSession().getFeatures().fileUpload() || model
				.getSession().getFeatures().folderActions())
				&& model.getSession().getDefaultPermissionMode()
						.hasWritePermission()) {
			addButton = new DropdownButton(actionListener, textProvider
					.getStrings().mainViewAddButtonTitle(),
					StyleConstants.MAIN_VIEW_HEADER_BUTTON_ADD);
			if (model.getSession().getFeatures().fileUpload())
				addButton.addAction(Action.addFile, textProvider.getStrings()
						.mainViewAddFileMenuItem());
			if (model.getSession().getFeatures().folderActions())
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
			listener.onShow();
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
		fileContextHandler.onItemSelected(file, list.getWidget(file,
				FileList.COLUMN_NAME));
	}

	public void showDirectoryContext(Directory directory) {
		directoryContextHandler.onItemSelected(directory, list.getWidget(
				directory, FileList.COLUMN_NAME));
	}

	public ActionButton getRefreshButton() {
		return refreshButton;
	}

	public DirectorySelector getDirectorySelector() {
		return directorySelector;
	}

	public DropdownButton getUsername() {
		return username;
	}

	public FileContextPopup getFileContext() {
		return fileContextPopup;
	}

	public DirectoryContextPopup getDirectoryContext() {
		return directoryContextPopup;
	}

	public void setPositionOnShow(DropdownPopup popup, Widget parent,
			int offsetWidth, int offsetHeight) {
		popup.setPopupPosition(parent.getAbsoluteLeft(), parent
				.getAbsoluteTop()
				+ parent.getOffsetHeight());
	}

	public void hideButtons() {
		buttonPanel.setVisible(false);
	}
}
