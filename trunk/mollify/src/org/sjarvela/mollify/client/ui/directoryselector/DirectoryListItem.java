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

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryListItem extends FlowPanel {
	private final String itemStyle;
	private final DirectoryListener listener;
	private final DirectoryProvider dataProvider;
	private final TextProvider textProvider;
	private final DirectoryListMenu menu;

	private final Directory currentDirectory;
	private final int level;

	private Label dropDown;
	private DirectoryListItemButton button;

	public DirectoryListItem(String itemStyle, Directory currentDirectory,
			int level, Directory parentDirectory, DirectoryProvider provider,
			DirectoryListener listener, TextProvider textProvider) {
		this.itemStyle = itemStyle;
		this.currentDirectory = currentDirectory;
		this.level = level;

		this.dataProvider = provider;
		this.listener = listener;
		this.textProvider = textProvider;

		this.setStylePrimaryName(StyleConstants.DIRECTORY_LISTITEM);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);

		this.add(createButton());
		this.add(createDropdownButton());

		this.menu = createMenu();
	}

	private Widget createButton() {
		button = new DirectoryListItemButton(itemStyle);
		button.setText(currentDirectory.getName());
		button.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				listener.onChangeToDirectory(level, currentDirectory);
			}
		});
		return button;
	}

	private Widget createDropdownButton() {
		dropDown = new Label();
		dropDown.setStyleName(StyleConstants.DIRECTORY_LISTITEM_DROPDOWN);
		if (itemStyle != null)
			dropDown.addStyleDependentName(itemStyle);

		dropDown.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				menu.show();
			}
		});
		return dropDown;
	}

	private DirectoryListMenu createMenu() {
		return new DirectoryListMenu(currentDirectory, level, dataProvider,
				listener, textProvider, this.getElement(), dropDown
						.getElement());
	}
}
