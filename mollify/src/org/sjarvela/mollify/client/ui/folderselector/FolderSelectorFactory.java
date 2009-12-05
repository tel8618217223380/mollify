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

import org.sjarvela.mollify.client.filesystem.foldermodel.FolderModel;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderModelProvider;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.ui.mainview.impl.MainViewModel;

public class FolderSelectorFactory implements FolderModelProvider {
	private final MainViewModel model;
	private final FolderListItemFactory listItemFactory;
	private final TextProvider textProvider;

	public FolderSelectorFactory(final MainViewModel model,
			final FileSystemService fileServices, TextProvider textProvider,
			FolderProvider folderProvider) {
		this.model = model;
		this.textProvider = textProvider;
		this.listItemFactory = new FolderListItemFactory(textProvider,
				folderProvider);
	}

	public FolderSelector createSelector() {
		return new FolderSelector(textProvider, this, listItemFactory);
	}

	public FolderModel getFolderModel() {
		return model.getFolderModel();
	}

}
