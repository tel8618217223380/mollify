/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.ActionToggleButton;
import org.sjarvela.mollify.client.ui.common.Coords;
import org.sjarvela.mollify.client.ui.common.Tooltip;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;
import org.sjarvela.mollify.client.ui.common.popup.DropdownPopup;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextPopupHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.filecontext.FileContextPopup;
import org.sjarvela.mollify.client.ui.fileitemcontext.filecontext.FileContextPopupFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.foldercontext.FolderContextPopup;
import org.sjarvela.mollify.client.ui.fileitemcontext.foldercontext.FolderContextPopupFactory;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.folderselector.FolderSelector;
import org.sjarvela.mollify.client.ui.folderselector.FolderSelectorFactory;
import org.sjarvela.mollify.client.ui.mainview.MainView;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultMainView extends Composite implements PopupPositioner,
		MainView {
	private final TextProvider textProvider;
	private final ActionListener actionListener;

	private final MainViewModel model;
	private final Panel buttonPanel;
	private final FolderSelector directorySelector;
	private final FileList list;

	private final FileContextPopup fileContextPopup;
	private final ContextPopupHandler<File> fileContextHandler;

	private final FolderContextPopup directoryContextPopup;
	private final ContextPopupHandler<Folder> directoryContextHandler;

	private MainViewHeader header;
	private DropdownButton addButton;
	private ActionButton refreshButton;
	private DropdownButton username;
	private ActionToggleButton selectButton;
	private DropdownButton selectOptionsButton;
	private DropdownButton fileActions;
	private ActionToggleButton dropBoxButton;

	List<ViewListener> viewListeners = new ArrayList<ViewListener>();

	public enum Action implements ResourceId {
		addFile, addDirectory, refresh, logout, changePassword, admin, editItemPermissions, selectMode, selectAll, selectNone, deleteMultiple, dropBox;
	};

	public DefaultMainView(MainViewModel model, TextProvider textProvider,
			ActionListener actionListener,
			FolderSelectorFactory directorySelectorFactory,
			FileContextPopupFactory fileContextPopupFactory,
			FolderContextPopupFactory directoryContextPopupFactory,
			DragAndDropManager dragAndDropManager) {
		this.model = model;
		this.textProvider = textProvider;
		this.actionListener = actionListener;

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName(StyleConstants.MAIN_VIEW_HEADER_BUTTONS);

		this.directorySelector = directorySelectorFactory.createSelector();
		this.list = new FileList(textProvider, dragAndDropManager);

		this.fileContextPopup = fileContextPopupFactory.createPopup();
		this.fileContextPopup.setPopupPositioner(this);
		this.fileContextHandler = new ContextPopupHandler<File>(
				fileContextPopup);

		this.directoryContextPopup = directoryContextPopupFactory.createPopup();
		this.fileContextPopup.setPopupPositioner(this);
		this.directoryContextHandler = new ContextPopupHandler<Folder>(
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
		directoryContextPopup.setFolderActionHandler(actionHandler);
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

		header = new MainViewHeader();

		Panel headerUpper = new HorizontalPanel();
		header.setUpper(headerUpper);

		headerUpper.setStyleName(StyleConstants.MAIN_VIEW_HEADER);

		Panel headerLower = new FlowPanel();
		header.setLower(headerLower);
		header.build();

		headerLower.setStyleName(StyleConstants.MAIN_VIEW_SUBHEADER);
		headerLower.add(selectButton);
		headerLower.add(selectOptionsButton);
		headerLower.add(fileActions);
		headerLower.add(dropBoxButton);
		headerLower.setVisible(false);

		if (addButton != null)
			buttonPanel.add(addButton);
		buttonPanel.add(refreshButton);
		buttonPanel.add(directorySelector);
		headerUpper.add(buttonPanel);

		if (model.getSession().isAuthenticationRequired()) {
			Panel loggedInPanel = new FlowPanel();
			loggedInPanel
					.setStyleName(StyleConstants.MAIN_VIEW_HEADER_LOGGED_IN);
			loggedInPanel.add(createUserName());
			headerUpper.add(loggedInPanel);
		}

		return header;
	}

	private Widget createUserName() {
		username = new DropdownButton(actionListener, "", "username", null,
				new PopupPositioner() {
					public void setPositionOnShow(DropdownPopup popup,
							Widget parent, int offsetWidth, int offsetHeight) {
						int x = Math.min(parent.getAbsoluteLeft(),
								DefaultMainView.this.getOffsetWidth()
										- offsetWidth);
						popup.setPopupPosition(x, parent.getAbsoluteTop());
					}
				});
		username.setStyleName(StyleConstants.MAIN_VIEW_HEADER_USERNAME);

		if (model.getSession().getDefaultPermissionMode().isAdmin()
				&& model.getSession().getFeatures().permissionUpdate()) {
			username.addAction(Action.editItemPermissions, textProvider
					.getStrings().mainViewEditPermissionsTitle());

			if (model.getSession().getFeatures().administration()) {
				username.addAction(Action.admin, textProvider.getStrings()
						.mainViewAdministrationTitle());
			}
			username.addSeparator();
		}

		if (model.getSession().getFeatures().changePassword()) {
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
		new Tooltip(StyleConstants.MAIN_VIEW_HEADER_BUTTON_TOOLTIP,
				textProvider.getStrings().mainViewRefreshButtonTooltip())
				.attach(refreshButton);
		refreshButton.setAction(actionListener, Action.refresh);

		selectButton = new ActionToggleButton(
				"Select", // TODO
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON_SELECT,
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON);
		selectButton.setAction(actionListener, Action.selectMode);
		selectOptionsButton = new DropdownButton(actionListener, "",
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_SELECT_OPTIONS,
				selectButton);
		selectOptionsButton.addAction(Action.selectAll, "All"); // TODO
		selectOptionsButton.addAction(Action.selectNone, "None"); // TODO

		fileActions = new DropdownButton(actionListener, "Actions",
				StyleConstants.MAIN_VIEW_HEADER_FILE_ACTIONS);
		fileActions.addAction(Action.deleteMultiple, "Delete..."); // TODO

		dropBoxButton = new ActionToggleButton(
				"Drop Box", // TODO
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON_DROPBOX,
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON);
		dropBoxButton.setAction(actionListener, Action.dropBox);

		if ((model.getSession().getFeatures().fileUpload() || model
				.getSession().getFeatures().folderActions())
				&& model.getSession().getDefaultPermissionMode()
						.hasWritePermission()) {
			addButton = new DropdownButton(actionListener, textProvider
					.getStrings().mainViewAddButtonTitle(),
					StyleConstants.MAIN_VIEW_HEADER_BUTTON_ADD);
			new Tooltip(StyleConstants.MAIN_VIEW_HEADER_BUTTON_TOOLTIP,
					textProvider.getStrings().mainViewAddButtonTooltip())
					.attach(addButton);

			if (model.getSession().getFeatures().fileUpload())
				addButton.addAction(Action.addFile, textProvider.getStrings()
						.mainViewAddFileMenuItem());
			if (model.getSession().getFeatures().folderActions())
				addButton.addAction(Action.addDirectory, textProvider
						.getStrings().mainViewAddDirectoryMenuItem());
		}
	}

	public void addFileListListener(GridListener listener) {
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

	public void showDirectoryContext(Folder directory) {
		directoryContextHandler.onItemSelected(directory, list.getWidget(
				directory, FileList.COLUMN_NAME));
	}

	public ActionButton getRefreshButton() {
		return refreshButton;
	}

	public FolderSelector getDirectorySelector() {
		return directorySelector;
	}

	public DropdownButton getUsername() {
		return username;
	}

	public FileContextPopup getFileContext() {
		return fileContextPopup;
	}

	public FolderContextPopup getDirectoryContext() {
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

	public Widget getViewWidget() {
		return this;
	}

	public ActionToggleButton selectModeButton() {
		return selectButton;
	}

	public void setSelectMode(boolean select) {
		list
				.setSelectionMode(select ? SelectionMode.Multi
						: SelectionMode.None);
	}

	public void updateFileSelection(List<FileSystemItem> selected) {
		fileActions.setEnabled(selected.size() > 0);
	}

	public void selectAll() {
		setSelectMode(true);
		selectButton.setDown(true);
		list.selectAll();
	}

	public void selectNone() {
		setSelectMode(true);
		selectButton.setDown(true);
		list.selectNone();
	}

	public Coords getDropboxLocation() {
		return new Coords(header.getAbsoluteTop() + header.getTotalHeight(),
				dropBoxButton.getAbsoluteLeft());
	}
}
