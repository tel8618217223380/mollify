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
import org.sjarvela.mollify.client.file.DirectoryModel;
import org.sjarvela.mollify.client.file.DirectoryModelProvider;
import org.sjarvela.mollify.client.file.DirectoryProvider;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.FileServices;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.ui.mainview.MainViewModel;

public class DirectorySelectorFactory implements DirectoryModelProvider {

	private Localizator localizator;
	private MainViewModel model;
	private DirectoryListItemFactory listItemFactory;

	public DirectorySelectorFactory(final MainViewModel model,
			final FileServices fileServices, Localizator localizator) {
		this.localizator = localizator;
		this.model = model;
		this.listItemFactory = new DirectoryListItemFactory(localizator,
				new DirectoryProvider() {

					public void getDirectories(Directory parent,
							ResultListener listener) {
						// if there is no parent, give root list
						if (parent.isEmpty()) {
							listener.onSuccess(model.getRootDirectories());
							return;
						}

						// no need to retrieve current view directories, they
						// are already retrieved
						if (parent.equals(model.getDirectoryModel()
								.getCurrentFolder())) {
							listener.onSuccess(model.getSubDirectories());
							return;
						}

						fileServices.getDirectories(parent, listener);
					}
				});
	}

	public DirectorySelector createSelector() {
		return new DirectorySelector(localizator, this, listItemFactory);
	}

	public DirectoryModel getDirectoryModel() {
		return model.getDirectoryModel();
	}

	public void setController(DirectoryController controller) {
		listItemFactory.setController(controller);
	}

}
