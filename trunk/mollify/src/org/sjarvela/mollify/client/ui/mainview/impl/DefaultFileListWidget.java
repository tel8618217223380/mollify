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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectController;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.filelist.FileList;

import com.google.gwt.user.client.ui.Widget;

public class DefaultFileListWidget implements FileListWidget {
	private FileList list;

	public DefaultFileListWidget(TextProvider textProvider,
			DragAndDropManager dragAndDropManager) {
		this.list = new FileList(textProvider, dragAndDropManager);
	}

	@Override
	public Widget getWidget() {
		return list;
	}

	@Override
	public void refresh() {
		list.refresh();
	}

	@Override
	public void removeAllRows() {
		list.removeAllRows();
	}

	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
		list.setSelectionMode(selectionMode);
	}

	@Override
	public void setSelectController(SelectController controller) {
		list.setSelectController(controller);
	}

	@Override
	public void selectAll() {
		list.selectAll();
	}

	@Override
	public void selectNone() {
		list.selectNone();
	}

	@Override
	public void addListener(GridListener listener) {
		list.addListener(listener);
	}

	@Override
	public void setComparator(GridComparator<FileSystemItem> comparator) {
		list.setComparator(comparator);
	}

	@Override
	public void setContent(List<FileSystemItem> items) {
		list.setContent(items);
	}
}
