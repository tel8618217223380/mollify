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

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.file.DirectoryController;
import org.sjarvela.mollify.client.file.DirectoryListener;
import org.sjarvela.mollify.client.file.DirectoryProvider;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryListItem extends HorizontalPanel implements
		DirectoryListener {
	private final DirectoryController controller;
	private final DirectoryProvider dataProvider;
	private final Localizator localizator;
	private final DirectoryListMenu menu;

	private Directory currentDirectory;
	private int level;
	private Directory parentDirectory;

	private Label dropDown;

	public DirectoryListItem(Directory currentDirectory, int level,
			Directory parentDirectory, DirectoryProvider provider,
			DirectoryController controller, Localizator localizator) {
		this.currentDirectory = currentDirectory;
		this.level = level;
		this.parentDirectory = parentDirectory;

		this.dataProvider = provider;
		this.controller = controller;
		this.localizator = localizator;

		this.setStyleName(StyleConstants.DIRECTORY_LIST);
		if (level == 0)
			this.addStyleName(StyleConstants.DIRECTORY_LIST_ROOT_LEVEL);

		this.add(createPadding(StyleConstants.DIRECTORY_LIST_PADDING_LEFT));
		this.add(createLabel());
		this.add(createDropdownButton());
		this.add(createPadding(StyleConstants.DIRECTORY_LIST_PADDING_RIGHT));
		this.menu = createMenu();
	}

	private Widget createPadding(String... classes) {
		Label label = new Label();
		for (String className : classes) {
			label.addStyleName(className);
		}
		return label;
	}

	private Label createLabel() {
		Label label = new Label(currentDirectory.getName());
		label.setStyleName(StyleConstants.DIRECTORY_LIST_LABEL);

		label.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				controller.changeToDirectory(level, currentDirectory);
			}
		});
		return label;
	}

	private Widget createDropdownButton() {
		dropDown = new Label();
		dropDown.setStyleName(StyleConstants.DIRECTORY_LIST_DROPDOWN);

		dropDown.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				menu.show();
			}
		});
		return dropDown;
	}

	private DirectoryListMenu createMenu() {
		return new DirectoryListMenu(parentDirectory, currentDirectory,
				dataProvider, this, localizator, this.getElement(), dropDown
						.getElement());
	}

	public void onDirectorySelected(Directory directory) {
		controller.changeToDirectory(level, directory);
	}
}
