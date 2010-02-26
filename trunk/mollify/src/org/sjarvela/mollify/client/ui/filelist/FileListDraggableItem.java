/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filelist;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;

import com.google.gwt.user.client.ui.Label;

public class FileListDraggableItem extends Label {

	private final FileSystemItem item;

	public FileListDraggableItem(FileSystemItem item) {
		super(item.getName());
		this.item = item;
		setStyleName(StyleConstants.FILE_LIST_ITEM_NAME);
		HoverDecorator.decorate(this);
	}

	public FileSystemItem getFileSystemItem() {
		return item;
	}

}
