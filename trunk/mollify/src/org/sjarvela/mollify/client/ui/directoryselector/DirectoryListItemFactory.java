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
import org.sjarvela.mollify.client.filesystem.DirectoryController;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.localization.Localizator;

public class DirectoryListItemFactory {
	private DirectoryProvider provider;
	private Localizator localizator;
	private DirectoryController controller;

	public DirectoryListItemFactory(Localizator localizator,
			DirectoryProvider provider) {
		this.localizator = localizator;
		this.provider = provider;
	}

	public DirectoryListItem createListItem(Directory current, int level,
			Directory parent) {
		return new DirectoryListItem(current, level, parent, provider,
				controller, localizator);
	}

	public void setController(DirectoryController controller) {
		this.controller = controller;
	}

}
