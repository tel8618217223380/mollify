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
import java.util.Map;
import java.util.Map.Entry;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.plugin.NativeAction;
import org.sjarvela.mollify.client.plugin.PluginEnvironment;
import org.sjarvela.mollify.client.plugin.PluginSystem;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.ActionToggleButton;
import org.sjarvela.mollify.client.ui.common.Coords;
import org.sjarvela.mollify.client.ui.common.HintTextBox;
import org.sjarvela.mollify.client.ui.common.Tooltip;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectController;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;
import org.sjarvela.mollify.client.ui.common.popup.DropdownPopup;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ContextPopupHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ItemContextPopup;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.folderselector.FolderSelector;
import org.sjarvela.mollify.client.ui.folderselector.FolderSelectorFactory;
import org.sjarvela.mollify.client.ui.mainview.MainView;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
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
	private final FolderSelector folderSelector;
	private final FileList list;
	private final FlowPanel listPanel;
	private final FlowPanel progress;

	private final ItemContextPopup itemContextPopup;
	private final ContextPopupHandler<FileSystemItem> itemContextHandler;
	private final List<ViewListener> viewListeners = new ArrayList();
	private final List<SearchListener> searchListeners = new ArrayList();

	private MainViewHeader header;
	private DropdownButton addButton;
	private ActionButton refreshButton;
	private DropdownButton username;
	private ActionToggleButton selectButton;
	private DropdownButton selectOptionsButton;
	private DropdownButton fileActions;
	private ActionToggleButton dropBoxButton;
	private HintTextBox searchField;

	private FlowPanel fileUrlContainer;
	private final PluginEnvironment pluginEnvironment;

	public enum Action implements ResourceId {
		addFile, addDirectory, refresh, logout, changePassword, admin, editItemPermissions, selectMode, selectAll, selectNone, copyMultiple, moveMultiple, deleteMultiple, dropBox, addToDropbox, retrieveUrl;
	};

	public DefaultMainView(final MainViewModel model,
			TextProvider textProvider, ActionListener actionListener,
			FolderSelectorFactory folderSelectorFactory,
			ItemContextPopup itemContextPopup,
			DragAndDropManager dragAndDropManager, PluginSystem pluginSystem,
			final PluginEnvironment pluginEnvironment) {
		this.model = model;
		this.textProvider = textProvider;
		this.actionListener = actionListener;
		this.pluginEnvironment = pluginEnvironment;

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName(StyleConstants.MAIN_VIEW_HEADER_BUTTONS);

		this.listPanel = new FlowPanel();
		this.listPanel.setStylePrimaryName(StyleConstants.FILE_LIST_PANEL);

		this.progress = new FlowPanel();
		this.progress.setStylePrimaryName(StyleConstants.FILE_LIST_PROGRESS);
		this.progress.setVisible(false);

		this.folderSelector = folderSelectorFactory.createSelector();
		this.list = new FileList(textProvider, dragAndDropManager) {
			@Override
			public List<String> getRowStyles(FileSystemItem t) {
				List<String> styles = super.getRowStyles(t);
				if (model.isShared(t))
					styles.add("shared");
				return styles;
			}
		};

		this.itemContextPopup = itemContextPopup;
		this.itemContextPopup.setPopupPositioner(this);
		this.itemContextHandler = new ContextPopupHandler<FileSystemItem>(
				itemContextPopup);

		this.searchField = new HintTextBox(
				textProvider.getText(Texts.mainViewSearchHint));
		this.searchField
				.setStylePrimaryName(StyleConstants.MAIN_VIEW_HEADER_SEARCH_FIELD);
		this.searchField.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == 13)
					onSearch(searchField.getValue());
			}
		});

		initWidget(createControls());
		setStyleName(StyleConstants.MAIN_VIEW);

		pluginSystem.addListener(new Callback() {
			@Override
			public void onCallback() {
				for (final Entry<String, NativeAction> e : pluginEnvironment
						.getActions().entrySet()) {
					username.addCallbackAction(e.getKey(), new Callback() {
						@Override
						public void onCallback() {
							e.getValue().onAction();
						}
					});
				}
			}
		});
	}

	private void onSearch(String text) {
		if (text.isEmpty())
			return;
		for (SearchListener listener : searchListeners)
			listener.onSearch(text);
	}

	public void addViewListener(ViewListener listener) {
		viewListeners.add(listener);
	}

	public void addSearchListener(SearchListener listener) {
		searchListeners.add(listener);
	}

	public void setContextHandler(FileSystemActionHandler actionHandler) {
		itemContextPopup.setActionHandler(actionHandler);
	}

	public FileList getList() {
		return list;
	}

	private Widget createControls() {
		Panel content = new VerticalPanel();
		content.getElement().setId("mollify-main-content");
		content.add(createHeader(listPanel));

		listPanel.add(list);
		content.add(listPanel);
		listPanel.add(progress);

		return content;
	}

	public Widget createFileUrlContainer() {
		fileUrlContainer = new FlowPanel();
		fileUrlContainer.getElement().setAttribute("style",
				"visibility:collapse; height: 0px;");
		return fileUrlContainer;
	}

	private Widget createHeader(Panel contentPanel) {
		createButtons();

		header = new MainViewHeader();

		Panel headerUpperPanel = new HorizontalPanel();
		headerUpperPanel.setStyleName(StyleConstants.MAIN_VIEW_HEADER_PANEL);

		Panel headerUpper = new HorizontalPanel();
		headerUpper.setStyleName(StyleConstants.MAIN_VIEW_HEADER);
		headerUpperPanel.add(headerUpper);

		Panel headerLower = new FlowPanel();
		headerLower.setStyleName(StyleConstants.MAIN_VIEW_SUBHEADER);

		headerLower.add(selectButton);
		headerLower.add(selectOptionsButton);
		headerLower.add(fileActions);
		headerLower.add(dropBoxButton);
		headerLower.add(createSearchField());

		if (addButton != null)
			buttonPanel.add(addButton);
		buttonPanel.add(refreshButton);
		buttonPanel.add(folderSelector);
		headerUpper.add(buttonPanel);

		if (model.getSession().isAuthenticationRequired()) {
			Panel loggedInPanel = new FlowPanel();
			loggedInPanel
					.setStyleName(StyleConstants.MAIN_VIEW_HEADER_LOGGED_IN);
			loggedInPanel.add(createUserName());
			headerUpper.add(loggedInPanel);
		}

		Panel headerLowerPanel = new FlowPanel();
		headerLowerPanel
				.setStyleName(StyleConstants.MAIN_VIEW_HEADER_LOWER_PANEL);
		headerLowerPanel.add(headerLower);
		header.build(headerUpperPanel, headerLower, headerLowerPanel);
		return header;
	}

	private Widget createSearchField() {
		FlowPanel p = new FlowPanel();
		p.setStylePrimaryName("mollify-header-search-container");

		FlowPanel l = new FlowPanel();
		l.setStylePrimaryName("mollify-header-search-container-left");
		p.add(l);

		FlowPanel c = new FlowPanel();
		c.setStylePrimaryName("mollify-header-search-container-center");
		c.add(searchField);
		p.add(c);

		FlowPanel r = new FlowPanel();
		r.setStylePrimaryName("mollify-header-search-container-right");
		p.add(r);

		return p;
	}

	private Widget createUserName() {
		username = new DropdownButton(actionListener, "", "username", null,
				new PopupPositioner() {
					public void setPositionOnShow(DropdownPopup popup,
							Widget parent, int offsetWidth, int offsetHeight) {
						int x = parent.getAbsoluteLeft()
								+ parent.getOffsetWidth() - offsetWidth;
						popup.setPopupPosition(x, parent.getAbsoluteTop());
					}
				});
		username.setStyleName(StyleConstants.MAIN_VIEW_HEADER_USERNAME);

		if (model.getSession().getDefaultPermissionMode().isAdmin()
				&& model.getSession().getFeatures().permissionUpdate()) {
			username.addAction(Action.editItemPermissions,
					textProvider.getText(Texts.mainViewEditPermissionsTitle));

			if (model.getSession().getFeatures().administration()) {
				username.addAction(Action.admin,
						textProvider.getText(Texts.mainViewAdministrationTitle));
			}
			username.addSeparator();
		}

		if (model.getSession().getFeatures().changePassword()) {
			username.addAction(Action.changePassword,
					textProvider.getText(Texts.mainViewChangePasswordTitle));
			username.addSeparator();
		}

		username.addAction(Action.logout,
				textProvider.getText(Texts.mainViewLogoutButtonTitle));

		return username;
	}

	private void createButtons() {
		refreshButton = new ActionButton(
				textProvider.getText(Texts.mainViewRefreshButtonTitle),
				StyleConstants.MAIN_VIEW_HEADER_BUTTON_REFRESH,
				StyleConstants.MAIN_VIEW_HEADER_BUTTON);
		new Tooltip(StyleConstants.MAIN_VIEW_HEADER_BUTTON_TOOLTIP,
				textProvider.getText(Texts.mainViewRefreshButtonTooltip))
				.attach(refreshButton);
		refreshButton.setAction(actionListener, Action.refresh);

		selectButton = new ActionToggleButton(
				textProvider.getText(Texts.mainViewSelectButton),
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON_SELECT,
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON);
		selectButton.setAction(actionListener, Action.selectMode);
		selectOptionsButton = new DropdownButton(actionListener, "",
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_SELECT_OPTIONS,
				selectButton);
		selectOptionsButton.addAction(Action.selectAll,
				textProvider.getText(Texts.mainViewSelectAll));
		selectOptionsButton.addAction(Action.selectNone,
				textProvider.getText(Texts.mainViewSelectNone));

		fileActions = new DropdownButton(actionListener,
				textProvider.getText(Texts.mainViewSelectActions),
				StyleConstants.MAIN_VIEW_HEADER_FILE_ACTIONS);
		fileActions.addAction(Action.addToDropbox,
				textProvider.getText(Texts.mainViewSelectActionAddToDropbox));
		fileActions.addSeparator();
		fileActions.addAction(Action.copyMultiple,
				textProvider.getText(Texts.fileActionCopyTitle));
		fileActions.addAction(Action.moveMultiple,
				textProvider.getText(Texts.fileActionMoveTitle));
		fileActions.addAction(Action.deleteMultiple,
				textProvider.getText(Texts.fileActionDeleteTitle));

		dropBoxButton = new ActionToggleButton(
				textProvider.getText(Texts.mainViewDropBoxButton),
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON_DROPBOX,
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON);
		dropBoxButton.setAction(actionListener, Action.dropBox);

		if ((model.getSession().getFeatures().fileUpload() || model
				.getSession().getFeatures().folderActions())) {
			addButton = new DropdownButton(actionListener,
					textProvider.getText(Texts.mainViewAddButtonTitle),
					StyleConstants.MAIN_VIEW_HEADER_BUTTON_ADD);
			new Tooltip(StyleConstants.MAIN_VIEW_HEADER_BUTTON_TOOLTIP,
					textProvider.getText(Texts.mainViewAddButtonTooltip))
					.attach(addButton);

			if (model.getSession().getFeatures().fileUpload())
				addButton.addAction(Action.addFile,
						textProvider.getText(Texts.mainViewAddFileMenuItem));
			if (model.getSession().getFeatures().folderActions())
				addButton.addAction(Action.addDirectory, textProvider
						.getText(Texts.mainViewAddDirectoryMenuItem));
			if (model.getSession().getFeatures().retrieveUrl())
				addButton
						.addAction(Action.retrieveUrl, textProvider
								.getText(Texts.mainViewRetrieveUrlMenuItem));

		}
	}

	public void setAddButtonVisible(boolean visible) {
		if (addButton != null)
			addButton.setVisible(visible);
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
		folderSelector.refresh();
		list.refresh();
	}

	public void clear() {
		list.removeAllRows();
		model.clear();
	}

	public void showFileContext(File file) {
		itemContextHandler.onItemSelected(file,
				list.getWidget(file, FileList.COLUMN_ID_NAME));
	}

	public void showFolderContext(Folder folder) {
		itemContextHandler.onItemSelected(folder,
				list.getWidget(folder, FileList.COLUMN_ID_NAME));
	}

	public ActionButton getRefreshButton() {
		return refreshButton;
	}

	public FolderSelector getFolderSelector() {
		return folderSelector;
	}

	public DropdownButton getUsername() {
		return username;
	}

	public ItemContextPopup getFileContext() {
		return itemContextPopup;
	}

	public void setPositionOnShow(DropdownPopup popup, Widget parent,
			int offsetWidth, int offsetHeight) {
		popup.setPopupPosition(parent.getAbsoluteLeft(),
				parent.getAbsoluteTop() + parent.getOffsetHeight());
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
		list.setSelectionMode(select ? SelectionMode.Multi : SelectionMode.None);
	}

	public void setListSelectController(SelectController controller) {
		list.setSelectController(controller);
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
		return new Coords(header.getOffsetWidth(), listPanel.getElement()
				.getAbsoluteTop());
	}

	public void refreshFileUrls(Map<String, String> urls) {
		fileUrlContainer.getElement().setInnerHTML("");

		for (Entry<String, String> e : urls.entrySet()) {
			Element a = DOM.createAnchor();
			a.setPropertyString("href", e.getValue());
			a.setInnerHTML(e.getKey());
			fileUrlContainer.getElement().appendChild(a);
		}
	}

	public void showProgress() {
		this.progress.setVisible(true);
	}

	public void hideProgress() {
		this.progress.setVisible(false);
	}

	public void clearSearchField() {
		this.searchField.clear();
	}

}
