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

import com.google.gwt.user.client.ui.Widget;

public class DefaultFileListGridWidget implements FileListWidget {
	private final TextProvider textProvider;
	private FileGrid grid;

	public DefaultFileListGridWidget(TextProvider textProvider) {
		this.textProvider = textProvider;
		this.grid = new FileGrid();
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
		// TODO Auto-generated method stub
	}

	@Override
	public Widget getWidget(FileSystemItem item, String columnId) {
		return grid.getWidget(item);
	}

	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelectController(SelectController controller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectNone() {
		// TODO Auto-generated method stub

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
	public void setComparator(GridComparator<FileSystemItem> comparator) {
		grid.setComparator(comparator);
	}

}
