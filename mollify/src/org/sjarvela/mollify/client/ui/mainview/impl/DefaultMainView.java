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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.ActionToggleButton;
import org.sjarvela.mollify.client.ui.common.ActionToggleButtonGroup;
import org.sjarvela.mollify.client.ui.common.Coords;
import org.sjarvela.mollify.client.ui.common.HintTextBox;
import org.sjarvela.mollify.client.ui.common.Tooltip;
import org.sjarvela.mollify.client.ui.common.TooltipPositioner;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectController;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;
import org.sjarvela.mollify.client.ui.common.popup.DropdownPopup;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ContextPopupHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ItemContextPopup;
import org.sjarvela.mollify.client.ui.folderselector.FolderSelector;
import org.sjarvela.mollify.client.ui.folderselector.FolderSelectorFactory;
import org.sjarvela.mollify.client.ui.mainview.MainView;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
	private final FlowPanel listPanel;
	private final FlowPanel progress;

	private final ItemContextPopup itemContextPopup;
	private final ContextPopupHandler<FileSystemItem> itemContextHandler;
	private final List<ViewListener> viewListeners = new ArrayList();
	private final List<SearchListener> searchListeners = new ArrayList();
	private final FileListWidgetFactory fileListViewFactory;
	private final DropBox dropBox;

	private VerticalPanel header;
	private DropdownButton addButton;
	private ActionButton refreshButton;
	private DropdownButton username;
	private ActionToggleButton selectButton;
	private DropdownButton selectOptionsButton;
	private DropdownButton fileActions;
	private ActionToggleButton slideBarButton;
	private HintTextBox searchField;
	private FlowPanel fileUrlContainer;
	private FileListWidget fileListView;
	private Widget content;
	private List<GridListener> listListeners = new ArrayList();

	public enum ViewType {
		list, gridSmall, gridLarge
	}

	public enum Action implements ResourceId {
		addFile, addDirectory, refresh, logout, changePassword, admin, editItemPermissions, selectMode, selectAll, selectNone, copyMultiple, moveMultiple, deleteMultiple, slideBar, addToDropbox, retrieveUrl, listView, gridViewSmall, gridViewLarge;
	};

	public DefaultMainView(MainViewModel model, TextProvider textProvider,
			ActionListener actionListener,
			FolderSelectorFactory folderSelectorFactory,
			ItemContextPopup itemContextPopup, DropBox dropBox,
			DragAndDropManager dragAndDropManager,
			FileListWidgetFactory fileViewFactory) {
		this.model = model;
		this.textProvider = textProvider;
		this.actionListener = actionListener;
		this.dropBox = dropBox;
		this.fileListViewFactory = fileViewFactory;

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName(StyleConstants.MAIN_VIEW_HEADER_BUTTONS);

		this.listPanel = new FlowPanel();
		this.listPanel.setStylePrimaryName(StyleConstants.FILE_LIST_PANEL);

		this.progress = new FlowPanel();
		this.progress.setStylePrimaryName(StyleConstants.FILE_LIST_PROGRESS);
		this.progress.setVisible(false);

		this.folderSelector = folderSelectorFactory.createSelector();

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

		content = createControls();
		initWidget(content);
		setStyleName(StyleConstants.MAIN_VIEW);

		setViewType(ViewType.list);
	}

	public void setViewType(ViewType type) {
		fileListView = fileListViewFactory.create(type);
		for (GridListener l : listListeners)
			fileListView.addListener(l);

		listPanel.clear();
		listPanel.add(fileListView.getWidget());
		listPanel.add(progress);
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

	public FileListWidget getFileWidget() {
		return fileListView;
	}

	private Widget createControls() {
		createButtons();

		Panel content = new VerticalPanel();
		content.getElement().setId("mollify-main-content");

		content.add(createHeader());
		content.add(createToolbar());

		Panel lowerContentPanel = new FlowPanel();
		lowerContentPanel.getElement()
				.setId("mollify-main-lower-content-panel");
		content.add(lowerContentPanel);

		Panel lowerContent = new FlowPanel();
		lowerContent.getElement().setId("mollify-main-lower-content");

		Panel headerLower = new FlowPanel();
		headerLower.setStyleName(StyleConstants.MAIN_VIEW_SUBHEADER);
		headerLower.add(createOptionsPanel());
		headerLower.add(slideBarButton);

		lowerContent.add(headerLower);
		lowerContent.add(listPanel);
		lowerContentPanel.add(lowerContent);

		Panel slideBar = new FlowPanel();
		slideBar.getElement().setId("mollify-mainview-slidebar");
		slideBar.add(createSelectBar());
		slideBar.add(createDropboxBar());
		lowerContentPanel.add(slideBar);

		return content;
	}

	private Widget createOptionsPanel() {
		Panel optionsPanel = new FlowPanel();
		optionsPanel.getElement().setId("mollify-mainview-options-panel");

		TooltipPositioner p = new TooltipPositioner() {
			@Override
			public Coords getPosition(Widget target, int top, int left,
					int popupWidth, int popupHeight) {
				int max = (content.getAbsoluteLeft() + content.getOffsetWidth())
						- popupWidth;
				return new Coords(Math.min(left, max), top);
			}
		};

		ActionToggleButton listViewButton = new ActionToggleButton("",
				"mollify-mainview-options-list",
				"mollify-mainview-options-button");
		listViewButton.setAction(actionListener, Action.listView);
		listViewButton.setDown(true);
		new Tooltip("mainview-options",
				textProvider.getText(Texts.mainViewOptionsListTooltip))
				.attachTo(listViewButton, p);
		optionsPanel.add(listViewButton);

		ActionToggleButton smallGridViewButton = new ActionToggleButton("",
				"mollify-mainview-options-grid-small",
				"mollify-mainview-options-button");
		smallGridViewButton.setAction(actionListener, Action.gridViewSmall);
		new Tooltip("mainview-options",
				textProvider.getText(Texts.mainViewOptionsGridSmallTooltip))
				.attachTo(smallGridViewButton, p);
		optionsPanel.add(smallGridViewButton);

		ActionToggleButton largeGridViewButton = new ActionToggleButton("",
				"mollify-mainview-options-grid-large",
				"mollify-mainview-options-button");
		largeGridViewButton.setAction(actionListener, Action.gridViewLarge);
		new Tooltip("mainview-options",
				textProvider.getText(Texts.mainViewOptionsGridLargeTooltip))
				.attachTo(largeGridViewButton, p);
		optionsPanel.add(largeGridViewButton);

		new ActionToggleButtonGroup(listViewButton, smallGridViewButton,
				largeGridViewButton);

		return optionsPanel;
	}

	private Panel createDropboxBar() {
		Panel dropboxPanel = new FlowPanel();
		dropboxPanel.setStylePrimaryName("mollify-mainview-slidebar-panel");
		dropboxPanel.getElement().setId("mollify-mainview-slidebar-dropbox");

		Label title = new Label(textProvider.getText(Texts.dropBoxTitle));
		title.setStylePrimaryName("title");

		dropboxPanel.add(title);
		dropboxPanel.add(dropBox.getWidget());
		return dropboxPanel;
	}

	private Panel createSelectBar() {
		Panel selectBar = new FlowPanel();
		selectBar.setStylePrimaryName("mollify-mainview-slidebar-panel");
		selectBar.getElement().setId("mollify-mainview-slidebar-select");

		Label title = new Label(
				textProvider.getText(Texts.mainViewSlideBarTitleSelect));
		title.setStylePrimaryName("title");

		selectBar.add(title);
		selectBar.add(selectButton);
		selectBar.add(selectOptionsButton);
		selectBar.add(fileActions);
		return selectBar;
	}

	private Widget createHeader() {
		Panel top = new FlowPanel();
		top.setStylePrimaryName("mollify-header-top");

		top.add(new HTML("<div id='mollify-logo'/>"));
		if (model.getSession().isAuthenticationRequired()) {
			Panel loggedInPanel = new FlowPanel();
			loggedInPanel
					.setStyleName(StyleConstants.MAIN_VIEW_HEADER_LOGGED_IN);
			loggedInPanel.add(createUserName());
			top.add(loggedInPanel);
		}
		return top;
	}

	public Widget createFileUrlContainer() {
		fileUrlContainer = new FlowPanel();
		fileUrlContainer.getElement().setAttribute("style",
				"visibility:collapse; height: 0px;");
		return fileUrlContainer;
	}

	private Widget createToolbar() {
		Panel toolbar = new HorizontalPanel();
		toolbar.setStyleName(StyleConstants.MAIN_VIEW_HEADER);
		if (addButton != null)
			buttonPanel.add(addButton);
		buttonPanel.add(refreshButton);
		buttonPanel.add(folderSelector);
		toolbar.add(buttonPanel);
		toolbar.add(createSearchField());
		return toolbar;
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
							Element parent, int offsetWidth, int offsetHeight) {
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
		refreshButton = new ActionButton("",
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

		slideBarButton = new ActionToggleButton("",
				StyleConstants.MAIN_VIEW_HEADER_TOGGLE_BUTTON_SLIDEBAR, null) {
			protected void updateStyle() {
				super.updateStyle();
				this.setText(down ? ">" : "<");
			};
		};
		slideBarButton.setAction(actionListener, Action.slideBar);

		if ((model.getSession().getFeatures().fileUpload() || model
				.getSession().getFeatures().folderActions())) {
			addButton = new DropdownButton(actionListener, "",
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
		listListeners.add(listener);
		fileListView.addListener(listener);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		for (ViewListener listener : viewListeners)
			listener.onShow();
	}

	public void refresh() {
		folderSelector.refresh();
	}

	public void clear() {
		fileListView.removeAllRows();
		model.clear();
	}

	public void showItemContext(FileSystemItem item, Element e) {
		itemContextHandler.onItemSelected(item, e);
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

	public ItemContextPopup getItemContext() {
		return itemContextPopup;
	}

	public void setPositionOnShow(DropdownPopup popup, Element parent,
			int offsetWidth, int offsetHeight) {
		int x = parent.getAbsoluteLeft();

		if ((x + offsetWidth) > content.getOffsetWidth())
			x = content.getOffsetWidth() - offsetWidth;

		popup.setPopupPosition(x,
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
		fileListView.setSelectionMode(select ? SelectionMode.Multi
				: SelectionMode.None);
	}

	public void setListSelectController(SelectController controller) {
		fileListView.setSelectController(controller);
	}

	public void updateFileSelection(List<FileSystemItem> selected) {
		fileActions.setEnabled(selected.size() > 0);
	}

	public void selectAll() {
		setSelectMode(true);
		selectButton.setDown(true);
		fileListView.selectAll();
	}

	public void selectNone() {
		setSelectMode(true);
		selectButton.setDown(true);
		fileListView.selectNone();
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
