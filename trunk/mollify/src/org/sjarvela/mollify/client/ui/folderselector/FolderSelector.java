/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.folderselector;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderModelProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.Tooltip;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FolderSelector extends FlowPanel implements FolderListener {
	private final FolderModelProvider folderModelProvider;
	private final FolderListItemFactory listItemFactory;
	private final TextProvider textProvider;
	private final List<FolderListener> listeners = new ArrayList();

	private final FolderListItem homeItem;
	private final Widget upButton;

	public FolderSelector(TextProvider textProvider,
			FolderModelProvider folderModelProvider,
			FolderListItemFactory listItemFactory) {
		this.textProvider = textProvider;
		this.folderModelProvider = folderModelProvider;
		this.listItemFactory = listItemFactory;
		this.setStyleName(StyleConstants.FOLDER_SELECTOR);

		this.upButton = createUpButton();
		this.homeItem = createHomeButton();
	}

	public void addListener(FolderListener listener) {
		this.listeners.add(listener);
	}

	private Label createUpButton() {
		final Label button = new Label(
				textProvider.getText(Texts.mainViewParentDirButtonTitle));
		button.setStyleName(StyleConstants.FOLDER_SELECTOR_BUTTON);
		button.getElement().setId(StyleConstants.FOLDER_SELECTOR_BUTTON_UP);

		new Tooltip(StyleConstants.MAIN_VIEW_HEADER_BUTTON_TOOLTIP,
				textProvider.getText(Texts.mainViewParentDirButtonTooltip))
				.attachTo(button);

		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FolderSelector.this.onMoveToParentFolder();
			}
		});
		return button;
	}

	private FolderListItem createHomeButton() {
		FolderListItem item = listItemFactory.createListItem(this,
				StyleConstants.FOLDER_LISTITEM_HOME, Folder.Empty, 0,
				Folder.Empty);
		item.addDropdownTooltip(new Tooltip(
				StyleConstants.MAIN_VIEW_HEADER_BUTTON_TOOLTIP, textProvider
						.getText(Texts.mainViewHomeButtonTooltip)));
		return item;
	}

	public void refresh() {
		updateHomeButton();

		this.clear();
		this.add(upButton);
		this.add(homeItem);

		FlowPanel items = new FlowPanel();
		items.setStyleName(StyleConstants.DIRECTORY_SELECTOR_ITEMS);

		for (FolderListItem item : createItems())
			items.add(item);
		this.add(items);
	}

	private void updateHomeButton() {
		homeItem.setStyle(folderModelProvider.getFolderModel().isRoot() ? StyleConstants.FOLDER_LISTITEM_HOME_LAST
				: StyleConstants.FOLDER_LISTITEM_HOME);
	}

	private List<FolderListItem> createItems() {
		ListIterator<Folder> list = folderModelProvider.getFolderModel()
				.getFolders();
		int level = 1;
		Folder parent = Folder.Empty;

		List<FolderListItem> items = new ArrayList();
		while (list.hasNext()) {
			Folder current = list.next();

			String style = (level == 1) ? StyleConstants.FOLDER_LISTITEM_ROOT_LEVEL
					: null;
			if (!list.hasNext()) {
				if (style == null)
					style = StyleConstants.FOLDER_LISTITEM_LAST;
				else
					style += "-" + StyleConstants.FOLDER_LISTITEM_LAST;
			}
			items.add(listItemFactory.createListItem(this, style, current,
					level, parent));

			level++;
			parent = current;
		}
		return items;
	}

	public void onChangeToFolder(int level, Folder directory) {
		for (FolderListener listener : listeners)
			listener.onChangeToFolder(level, directory);
	}

	public void onMoveToParentFolder() {
		for (FolderListener listener : listeners)
			listener.onMoveToParentFolder();
	}
}
