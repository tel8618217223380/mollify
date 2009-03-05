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

import java.util.ListIterator;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryModelProvider;
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class DirectorySelector extends HorizontalPanel {
	private DefaultTextProvider localizator;
	private final DirectoryModelProvider directoryModelProvider;
	private final DirectoryListItemFactory listItemFactory;

	public DirectorySelector(DefaultTextProvider localizator,
			DirectoryModelProvider directoryModelProvider,
			DirectoryListItemFactory listItemFactory) {
		this.directoryModelProvider = directoryModelProvider;
		this.localizator = localizator;
		this.listItemFactory = listItemFactory;
		this.setStyleName(StyleConstants.DIRECTORY_SELECTOR);
	}

	public void refresh() {
		this.clear();

		ListIterator<Directory> list = directoryModelProvider
				.getDirectoryModel().getDirectories();
		int level = 0;
		Directory parent = Directory.Empty;

		while (list.hasNext()) {
			Directory current = list.next();
			this.add(listItemFactory.createListItem(current, level, parent));
			if (list.hasNext())
				addSeparator();

			level++;
			parent = current;
		}
	}

	private void addSeparator() {
		Label separator = new Label(localizator.getStrings()
				.directorySelectorSeparatorLabel());
		separator.setStyleName(StyleConstants.DIRECTORY_SELECTOR_SEPARATOR);
		this.add(separator);
	}

}
