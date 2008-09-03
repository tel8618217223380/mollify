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

import org.sjarvela.mollify.client.DirectoryListener;
import org.sjarvela.mollify.client.DirectoryProvider;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.DropdownPopup;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryListMenu extends DropdownPopup implements ResultListener {
	private Panel container;

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
		super(parent, opener);

		this.directoryProvider = directoryProvider;
		this.currentDirectory = currentDirectory;
		this.parentDirectory = parentDirectory;
		this.listener = listener;
		this.localizator = localizator;

		container = new VerticalPanel();
		container.add(createWaitLabel(localizator));
		setWidget(container);

		this.setStyleName(StyleConstants.DIRECTORY_LIST_MENU);
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

	public void onError(ServiceError error) {
		initialized = true;
		container.clear();

		Label failedLabel = new Label(error.getMessage(localizator));
		failedLabel.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_ERROR);
		container.add(failedLabel);
	}

	public void onSuccess(JavaScriptObject result) {
		JsArray<Directory> directories = result.cast();
		initialized = true;
		container.clear();

		int count = 0;
		for (int i = 0, n = directories.length(); i < n; ++i) {
			Directory current = directories.get(i);
			if (current.equals(this.currentDirectory))
				continue;
			container.add(createDirectoryLabel(current));
			count++;
		}

		if (count == 0)
			addNoDirectoriesLabel();
	}

	private void addNoDirectoriesLabel() {
		Label label = new Label(localizator.getStrings()
				.directorySelectorMenuNoItemsText());
		label.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_ITEM_NONE);
		container.add(label);
	}

	private Widget createDirectoryLabel(final Directory directory) {
		Label label = new Label(directory.getName());
		label.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_ITEM);
		final DirectoryListMenu instance = this;

		label.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				instance.hide();
				listener.onDirectorySelected(directory);
			}
		});
		return label;
	}
}
