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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryListener;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.request.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.ActionId;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.DropdownPopupMenu;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryListMenu extends DropdownPopupMenu<Directory> implements
		ResultListener {
	boolean initialized = false;
	boolean dataRequested = false;

	private DirectoryProvider directoryProvider;
	private Directory parentDirectory;
	private Directory currentDirectory;
	private DirectoryListener listener;
	private Localizator localizator;

	public DirectoryListMenu(Directory parentDirectory,
			Directory currentDirectory, DirectoryProvider directoryProvider,
			DirectoryListener listener, Localizator localizator,
			Element parent, Element opener) {
		super(null, parent, opener);

		this.directoryProvider = directoryProvider;
		this.currentDirectory = currentDirectory;
		this.parentDirectory = parentDirectory;
		this.listener = listener;
		this.localizator = localizator;

		this.addStyleName(StyleConstants.DIRECTORY_LIST_MENU);
		addItem(createWaitLabel(localizator));
	}

	private Label createWaitLabel(Localizator localizator) {
		Label waitLabel = new Label(localizator.getStrings()
				.directorySelectorMenuPleaseWait());
		waitLabel.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_WAIT);
		return waitLabel;
	}

	@Override
	protected void onShow() {
		if (!initialized && !dataRequested)
			requestData();
	}

	private void requestData() {
		directoryProvider.getDirectories(parentDirectory, this);
		dataRequested = true;
	}

	public void onFail(ServiceError error) {
		initialized = true;
		removeAllMenuItems();

		Label failedLabel = new Label(error.getType().getMessage(localizator));
		failedLabel.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_ERROR);
		addItem(failedLabel);
	}

	public void onSuccess(Object... result) {
		List<Directory> directories = (List<Directory>) result[0];
		initialized = true;
		removeAllMenuItems();

		int count = 0;
		for (Directory dir : directories) {
			if (dir.getId().equals(this.currentDirectory.getId()))
				continue;
			addMenuAction(null, dir);
			count++;
		}

		if (count == 0)
			addNoDirectoriesLabel();
	}

	private void addNoDirectoriesLabel() {
		Label label = new Label(localizator.getStrings()
				.directorySelectorMenuNoItemsText());
		label.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_ITEM_NONE);
		addItem(label);
	}

	@Override
	protected Label createMenuItemWidget(final ActionId action,
			final Directory item) {
		Label label = createMenuItemWidget(item.getName());
		label.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				listener.onDirectorySelected(item);
			}
		});
		return label;
	}

}
