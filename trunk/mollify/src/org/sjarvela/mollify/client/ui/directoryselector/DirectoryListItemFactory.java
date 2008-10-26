package org.sjarvela.mollify.client.ui.directoryselector;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.file.DirectoryController;
import org.sjarvela.mollify.client.file.DirectoryProvider;
import org.sjarvela.mollify.client.localization.Localizator;

public class DirectoryListItemFactory {
	private DirectoryController controller;
	private DirectoryProvider provider;
	private Localizator localizator;

	public DirectoryListItemFactory(DirectoryController controller,
			Localizator localizator, DirectoryProvider provider) {
		this.controller = controller;
		this.localizator = localizator;
		this.provider = provider;
	}

	public DirectoryListItem createListItem(Directory current, int level,
			Directory parent) {
		return new DirectoryListItem(current, level, parent, controller,
				provider, localizator);
	}

}
