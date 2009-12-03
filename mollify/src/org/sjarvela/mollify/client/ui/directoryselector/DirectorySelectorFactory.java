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

import org.sjarvela.mollify.client.filesystem.foldermodel.FolderModel;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderModelProvider;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.ui.mainview.impl.MainViewModel;

public class DirectorySelectorFactory implements FolderModelProvider {
	private final MainViewModel model;
	private final DirectoryListItemFactory listItemFactory;
	private final TextProvider textProvider;

	public DirectorySelectorFactory(final MainViewModel model,
			final FileSystemService fileServices, TextProvider textProvider,
			FolderProvider directoryProvider) {
		this.model = model;
		this.textProvider = textProvider;
		this.listItemFactory = new DirectoryListItemFactory(textProvider,
				directoryProvider);
	}

	public DirectorySelector createSelector() {
		return new DirectorySelector(textProvider, this, listItemFactory);
	}

	public FolderModel getFolderModel() {
		return model.getDirectoryModel();
	}

}
