package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.file.DirectoryModel;
import org.sjarvela.mollify.client.file.DirectoryModelProvider;
import org.sjarvela.mollify.client.file.DirectoryProvider;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.ui.directoryselector.DirectoryListItemFactory;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelector;

public class DirectorySelectorFactory implements DirectoryModelProvider {

	private Localizator localizator;
	private FileViewModel model;
	private DirectoryListItemFactory listItemFactory;

	public DirectorySelectorFactory(final FileViewModel model,
			Localizator localizator) {
		this.localizator = localizator;
		this.model = model;
		this.listItemFactory = new DirectoryListItemFactory(null, localizator,
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

						// model.getSubDirectoriesUnderDirectory(parent.getId(),
						// listener);
					}
				});
	}

	public DirectorySelector createSelector() {
		return new DirectorySelector(localizator, this, listItemFactory);
	}

	public DirectoryModel getDirectoryModel() {
		return model.getDirectoryModel();
	}

}
