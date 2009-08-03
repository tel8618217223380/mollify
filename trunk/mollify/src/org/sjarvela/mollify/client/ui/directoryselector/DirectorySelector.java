/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.directoryselector;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryModelProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.Tooltip;
import org.sjarvela.mollify.client.ui.common.TooltipTarget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;

public class DirectorySelector extends FlowPanel implements DirectoryListener {
	private final DirectoryModelProvider directoryModelProvider;
	private final DirectoryListItemFactory listItemFactory;
	private final TextProvider textProvider;
	private final List<DirectoryListener> listeners = new ArrayList();

	private final DirectoryListItem homeItem;
	private final Button upButton;

	public DirectorySelector(TextProvider textProvider,
			DirectoryModelProvider directoryModelProvider,
			DirectoryListItemFactory listItemFactory) {
		this.textProvider = textProvider;
		this.directoryModelProvider = directoryModelProvider;
		this.listItemFactory = listItemFactory;
		this.setStyleName(StyleConstants.DIRECTORY_SELECTOR);

		this.upButton = createUpButton();
		this.homeItem = createHomeButton();
	}

	public void addListener(DirectoryListener listener) {
		this.listeners.add(listener);
	}

	private Button createUpButton() {
		final Button button = new Button(textProvider.getStrings()
				.mainViewParentDirButtonTitle());
		button.setStyleName(StyleConstants.DIRECTORY_SELECTOR_BUTTON);
		button.getElement().setId(StyleConstants.DIRECTORY_SELECTOR_BUTTON_UP);

		new Tooltip(StyleConstants.MAIN_VIEW_HEADER_BUTTON_TOOLTIP,
				textProvider.getStrings().mainViewParentDirButtonTooltip())
				.attach(new TooltipTarget() {
					public FocusWidget getWidget() {
						return button;
					}

					public boolean showTooltip() {
						return true;
					}
				});

		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DirectorySelector.this.onMoveToParentDirectory();
			}
		});
		return button;
	}

	private DirectoryListItem createHomeButton() {
		DirectoryListItem item = listItemFactory.createListItem(this,
				StyleConstants.DIRECTORY_LISTITEM_HOME, Directory.Empty, 0,
				Directory.Empty);
		item.addDropdownTooltip(new Tooltip(
				StyleConstants.MAIN_VIEW_HEADER_BUTTON_TOOLTIP, textProvider
						.getStrings().mainViewHomeButtonTooltip()));
		return item;
	}

	public void refresh() {
		this.clear();

		this.add(upButton);
		this.add(homeItem);

		FlowPanel items = new FlowPanel();
		items.setStyleName(StyleConstants.DIRECTORY_SELECTOR_ITEMS);

		for (DirectoryListItem item : createItems())
			items.add(item);
		this.add(items);
	}

	private List<DirectoryListItem> createItems() {
		ListIterator<Directory> list = directoryModelProvider
				.getDirectoryModel().getDirectories();
		int level = 1;
		Directory parent = Directory.Empty;

		List<DirectoryListItem> items = new ArrayList();
		while (list.hasNext()) {
			Directory current = list.next();

			String style = (level == 1) ? StyleConstants.DIRECTORY_LISTITEM_ROOT_LEVEL
					: null;
			if (!list.hasNext()) {
				if (style == null)
					style = StyleConstants.DIRECTORY_LISTITEM_LAST;
				else
					style += "-" + StyleConstants.DIRECTORY_LISTITEM_LAST;
			}
			items.add(listItemFactory.createListItem(this, style, current,
					level, parent));

			level++;
			parent = current;
		}
		return items;
	}

	public void onChangeToDirectory(int level, Directory directory) {
		for (DirectoryListener listener : listeners)
			listener.onChangeToDirectory(level, directory);
	}

	public void onMoveToParentDirectory() {
		for (DirectoryListener listener : listeners)
			listener.onMoveToParentDirectory();
	}
}
