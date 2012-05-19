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

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.Tooltip;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class FolderListItem extends FlowPanel {
	private final FolderListener listener;
	private final FolderProvider dataProvider;
	private final TextProvider textProvider;

	private final Folder current;
	private final int level;

	private String itemStyle;
	private FolderListItemButton button = null;

	public FolderListItem(String itemStyle, Folder currentDirectory, int level,
			Folder parentDirectory, FolderProvider provider,
			FolderListener listener, TextProvider textProvider) {
		this.current = currentDirectory;
		this.level = level;

		this.dataProvider = provider;
		this.listener = listener;
		this.textProvider = textProvider;

		setStyle(itemStyle);
		this.add(createButton());
	}

	public void setStyle(String itemStyle) {
		this.itemStyle = itemStyle;
		this.setStyleName(StyleConstants.DIRECTORY_LISTITEM);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);
		if (button != null)
			button.setStyle(itemStyle);
	}

	private Widget createButton() {
		button = new FolderListItemButton(itemStyle);
		button.setText(current.getName());
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				listener.onChangeToFolder(level, current);
			}
		});
		button.setDropdownMenu(createMenu(button.getElement()));
		return button;
	}

	private FolderListMenu createMenu(Element popupElement) {
		return new FolderListMenu(itemStyle, current, level + 1, dataProvider,
				listener, textProvider, this);
	}

	public void addDropdownTooltip(Tooltip tooltip) {
		button.addDropdownTooltip(tooltip);
	}
}
