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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.popup.DropdownPopupMenu;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FolderListMenu extends DropdownPopupMenu<Folder> implements
		ResultListener<List<Folder>> {
	private final int level;
	private final Folder current;
	private final FolderProvider folderProvider;
	private final FolderListener listener;
	private final TextProvider textProvider;

	boolean initialized = false;
	boolean dataRequested = false;

	public FolderListMenu(String itemStyle, Folder currentDirectory, int level,
			FolderProvider directoryProvider, FolderListener listener,
			TextProvider textProvider, Widget parent) {
		super(null, parent, null);

		this.level = level;
		this.folderProvider = directoryProvider;
		this.current = currentDirectory;
		this.listener = listener;
		this.textProvider = textProvider;

		this.setStylePrimaryName(StyleConstants.DIRECTORY_LIST_MENU);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);
		addItem(createWaitLabel());
	}

	private Label createWaitLabel() {
		Label waitLabel = new Label(
				textProvider.getText(Texts.directorySelectorMenuPleaseWait));
		waitLabel.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_WAIT);
		return waitLabel;
	}

	@Override
	protected void onShow() {
		if (!initialized && !dataRequested)
			requestData();
	}

	private void requestData() {
		folderProvider.getFolders(current, this);
		dataRequested = true;
	}

	public void onFail(ServiceError error) {
		initialized = true;
		removeAllMenuItems();

		Label failedLabel = new Label(error.getType().getMessage(textProvider));
		failedLabel.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_ERROR);
		addItem(failedLabel);
	}

	public void onSuccess(List<Folder> list) {
		initialized = true;
		removeAllMenuItems();

		List<Folder> folders = new ArrayList(list);
		Collections.sort(folders, new Comparator<Folder>() {
			public int compare(Folder d1, Folder d2) {
				return d1.getName().compareToIgnoreCase(d2.getName());
			}
		});
		int count = 0;
		for (Folder dir : folders) {
			if (dir.getId().equals(this.current.getId()))
				continue;
			addMenuAction(null, dir);
			count++;
		}

		if (count == 0)
			addNoFoldersLabel();
	}

	private void addNoFoldersLabel() {
		Label label = new Label(
				textProvider.getText(Texts.directorySelectorMenuNoItemsText));
		label.setStyleName(StyleConstants.DIRECTORY_LIST_MENU_ITEM_NONE);
		addItem(label);
	}

	@Override
	protected Label createMenuItemWidget(final ResourceId action,
			final Folder item) {
		Label label = createMenuItemWidget(item.getName());
		label.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				listener.onChangeToFolder(level, item);
			}
		});
		return label;
	}
}
