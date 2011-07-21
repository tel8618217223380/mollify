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
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectController;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;

import com.google.gwt.user.client.ui.Widget;

public class DefaultFileListGridWidget implements FileListWidget {
	private FileGrid grid;

	public DefaultFileListGridWidget(boolean thumbnails,
			FileSystemService service, boolean small) {
		this.grid = new FileGrid(thumbnails, service, small ? "small" : "large");
	}

	@Override
	public Widget getWidget() {
		return grid;
	}

	@Override
	public void refresh() {
		grid.refresh();
	}

	@Override
	public void removeAllRows() {
		grid.clear();
	}

	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
		grid.setSelectMode(!SelectionMode.None.equals(selectionMode));
	}

	@Override
	public void setSelectController(SelectController controller) {
		grid.setSelectController(controller);
	}

	@Override
	public void selectAll() {
		grid.selectAll();
	}

	@Override
	public void selectNone() {
		grid.selectNone();
	}

	@Override
	public void addListener(GridListener listener) {
		grid.addListener(listener);
	}

	@Override
	public void setContent(List<FileSystemItem> items) {
		grid.setContent(items);
	}

	@Override
	public void sortColumn(String columnId, SortOrder sort) {
		// TODO Auto-generated method stub
	}
}
