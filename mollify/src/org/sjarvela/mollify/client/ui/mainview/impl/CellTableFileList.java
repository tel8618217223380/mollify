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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectController;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.filelist.FileList;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CellTableFileList implements FileListWidget, CellListener {
	private static String COL_NAME = "name";
	private static String COL_TYPE = "type";
	private static String COL_SIZE = "size";

	private CellTable<FileSystemItem> table;
	private List<FileSystemItem> items;
	private GridListener listener;
	private CellTableFileListColumn nameColumn;
	private CellTableFileListColumn typeColumn;
	private CellTableFileListColumn sizeColumn;

	public CellTableFileList(TextProvider textProvider) {
		table = new CellTable();

		nameColumn = new CellTableFileListColumn(new CellTableFileListCell(
				COL_NAME, this));
		table.addColumn(nameColumn,
				textProvider.getText(Texts.fileListColumnTitleName), null);

		typeColumn = new CellTableFileListColumn(new CellTableFileListCell(
				COL_TYPE, this));
		table.addColumn(typeColumn,
				textProvider.getText(Texts.fileListColumnTitleType), null);

		sizeColumn = new CellTableFileListColumn(new CellTableFileListCell(
				COL_SIZE, this));
		table.addColumn(sizeColumn,
				textProvider.getText(Texts.fileListColumnTitleSize), null);
	}

	@Override
	public void onBrowserEvent(String id, Context context, Element e,
			FileSystemItem item, NativeEvent event) {
		if (event.getType().equals("click")) {
			onClick(id, item);
		}
	}

	private void onClick(String id, FileSystemItem item) {
		if (COL_NAME.equals(id)) {
			listener.onColumnClicked(item, FileList.COLUMN_ID_NAME);
		}
	}

	@Override
	public void onRender(String id, FileSystemItem item, SafeHtmlBuilder sb) {
		if (COL_NAME.equals(id)) {
			sb.appendEscaped(item.getName());
		} else if (COL_TYPE.equals(id)) {
			String type = "";
			if (item.isFile())
				type = ((File) item).getExtension();
			sb.appendEscaped(type);
		} else if (COL_TYPE.equals(id)) {
			String type = "";
			if (item.isFile())
				type = ((File) item).getExtension();
			sb.appendEscaped(type);
		}
	}

	@Override
	public Widget getWidget() {
		return table;
	}

	@Override
	public void refresh() {
		this.setContent(items);
	}

	@Override
	public void removeAllRows() {
		this.setContent(Collections.EMPTY_LIST);
	}

	@Override
	public Widget getWidget(FileSystemItem item, String columnId) {
		return new Label();
	}

	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
	}

	@Override
	public void setSelectController(SelectController controller) {
	}

	@Override
	public void selectAll() {
	}

	@Override
	public void selectNone() {
	}

	@Override
	public void addListener(GridListener listener) {
		this.listener = listener;
	}

	@Override
	public void setContent(List<FileSystemItem> items) {
		this.items = items;
		table.setRowCount(items.size());
		table.setRowData(items);
		this.listener.onRendered();
	}

	@Override
	public void setComparator(final GridComparator<FileSystemItem> c) {
		ListHandler<FileSystemItem> columnSortHandler = new ListHandler(items);
		columnSortHandler.setComparator(nameColumn,
				new Comparator<FileSystemItem>() {
					public int compare(FileSystemItem o1, FileSystemItem o2) {
						return c.compare(o1, o2);
					}
				});
		table.addColumnSortHandler(columnSortHandler);
	}

}
