/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.dnd.DragController;
import org.sjarvela.mollify.client.ui.dnd.DragDataProvider;
import org.sjarvela.mollify.client.ui.filelist.DraggableFileSystemItem;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FileItemDragController implements DragController {
	private final TextProvider textProvider;
	private DragDataProvider<FileSystemItem> dataProvider;

	public FileItemDragController(TextProvider textProvider) {
		this.textProvider = textProvider;
	}

	@Override
	public boolean useProxy() {
		return true;
	}

	@Override
	public Widget createProxy(DragContext context) {
		FileSystemItem item = ((DraggableFileSystemItem) context.selectedWidgets
				.get(0)).getSourceItem();
		List<FileSystemItem> items = new ArrayList(dataProvider
				.getSelectedItems());
		if (!items.contains(item))
			items.add(item);

		((DraggableFileSystemItem) context.selectedWidgets.get(0))
				.setItems(items);

		return createProxy(items);
	}

	private Label createProxy(List<FileSystemItem> items) {
		Label proxy = new Label(items.size() == 1 ? items.get(0).getName()
				: textProvider.getMessages().dragMultipleItems(items.size()));
		proxy.setStylePrimaryName(StyleConstants.FILE_ITEM_DRAG);
		return proxy;
	}

	@Override
	public void setDataProvider(DragDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

}
