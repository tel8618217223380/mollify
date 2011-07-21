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
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectController;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.filelist.FileList;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Widget;

public class CellTableFileList implements FileListWidget, CellListener {
	// private final Logger log = Logger.getLogger(CellTableFileList.class
	// .getName());

	interface Template extends SafeHtmlTemplates {
		@com.google.gwt.safehtml.client.SafeHtmlTemplates.Template("<div class=\"mollify-filelist-item-name\">{0}<div>")
		SafeHtml nameCellContent(String name);

		@com.google.gwt.safehtml.client.SafeHtmlTemplates.Template("<div class=\"mollify-filelist-item-size\">{0}<div>")
		SafeHtml sizeCellContent(String size);

		@com.google.gwt.safehtml.client.SafeHtmlTemplates.Template("<div class=\"mollify-filelist-item-type\">{0}<div>")
		SafeHtml typeCellContent(String type);
	}

	private static String COL_ICON = "icon";
	private static String COL_NAME = "name";
	private static String COL_TYPE = "type";
	private static String COL_SIZE = "size";

	private final CellTable<FileSystemItem> table;
	private final CellTableFileListColumn iconColumn;
	private final CellTableFileListColumn nameColumn;
	private final CellTableFileListColumn typeColumn;
	private final CellTableFileListColumn sizeColumn;
	private final TextProvider textProvider;
	private final Template template;

	private List<FileSystemItem> items = Collections.EMPTY_LIST;
	private GridListener listener;

	public CellTableFileList(TextProvider textProvider) {
		this.textProvider = textProvider;

		template = GWT.create(Template.class);
		table = new CellTable();
		table.setStylePrimaryName(StyleConstants.FILE_LIST);
		table.addStyleDependentName("v2");

		iconColumn = new CellTableFileListColumn(new CellTableFileListCell(
				COL_ICON, this));
		iconColumn.setSortable(false);
		table.addColumn(iconColumn, "", null);

		nameColumn = new CellTableFileListColumn(new CellTableFileListCell(
				COL_NAME, this));
		nameColumn.setSortable(true);
		table.addColumn(nameColumn,
				textProvider.getText(Texts.fileListColumnTitleName), null);

		typeColumn = new CellTableFileListColumn(new CellTableFileListCell(
				COL_TYPE, this));
		typeColumn.setSortable(true);
		table.addColumn(typeColumn,
				textProvider.getText(Texts.fileListColumnTitleType), null);

		sizeColumn = new CellTableFileListColumn(new CellTableFileListCell(
				COL_SIZE, this));
		sizeColumn.setSortable(true);
		table.addColumn(sizeColumn,
				textProvider.getText(Texts.fileListColumnTitleSize), null);

		setupSorting();

		table.getColumnSortList().push(nameColumn);

		table.setRowStyles(new RowStyles<FileSystemItem>() {
			@Override
			public String getStyleNames(FileSystemItem item, int i) {
				if (item.isFile())
					return getFileStyles((File) item, i);
				return getFolderStyles((Folder) item, i);
			}
		});
		table.addColumnStyleName(0, "mollify-filelist-column-icon");
		table.addColumnStyleName(1, "mollify-filelist-column-name");
		table.addColumnStyleName(2, "mollify-filelist-column-type");
		table.addColumnStyleName(3, "mollify-filelist-column-size");
	}

	private void setupSorting() {
		ListHandler<FileSystemItem> columnSortHandler = new ListHandler(items);
		columnSortHandler.setComparator(nameColumn,
				new Comparator<FileSystemItem>() {
					public int compare(FileSystemItem o1, FileSystemItem o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
		columnSortHandler.setComparator(typeColumn,
				new Comparator<FileSystemItem>() {
					public int compare(FileSystemItem o1, FileSystemItem o2) {
						String t1 = o1.isFile() ? ((File) o1).getExtension()
								: "";
						String t2 = o2.isFile() ? ((File) o2).getExtension()
								: "";
						return t1.compareTo(t2);
					}
				});
		columnSortHandler.setComparator(sizeColumn,
				new Comparator<FileSystemItem>() {
					public int compare(FileSystemItem o1, FileSystemItem o2) {
						long s1 = o1.isFile() ? ((File) o1).getSize() : 0;
						long s2 = o2.isFile() ? ((File) o2).getSize() : 0;
						return (s1 == s2) ? 0 : (s1 > s2 ? 1 : -1);
					}
				});
		table.addColumnSortHandler(columnSortHandler);
	}

	@Override
	public void onBrowserEvent(String id, Context context, Element e,
			FileSystemItem item, NativeEvent event) {
		if (event.getType().equals("click")) {
			onClick(id, item, e);
		} else if (event.getType().equals("mouseover")) {
			Element tr = e.getParentElement().getParentElement();
			tr.addClassName("hover");
		} else if (event.getType().equals("mouseout")) {
			Element tr = e.getParentElement().getParentElement();
			tr.removeClassName("hover");
		}
	}

	private void onClick(String id, FileSystemItem item, Element e) {
		if (COL_NAME.equals(id)) {
			listener.onColumnClicked(item, FileList.COLUMN_ID_NAME, e
					.getFirstChildElement().getFirstChildElement());
		} else if (COL_ICON.equals(id)) {
			Element nameElement = e.getParentElement().getNextSiblingElement()
					.getFirstChildElement();
			listener.onIconClicked(item, nameElement);
		}
	}

	@Override
	public void onRender(String id, FileSystemItem item, SafeHtmlBuilder sb) {
		if (COL_ICON.equals(id)) {
			sb.appendHtmlConstant("<div class=\""
					+ (item.isFile() ? "mollify-filelist-row-file-icon"
							: "mollify-filelist-row-directory-icon") + "\"/>");
		} else if (COL_NAME.equals(id)) {
			sb.append(template.nameCellContent(item.getName()));
		} else if (COL_TYPE.equals(id)) {
			String type = "";
			if (item.isFile())
				type = ((File) item).getExtension();
			sb.append(template.typeCellContent(type));
		} else if (COL_SIZE.equals(id)) {
			String size = "";
			if (item.isFile())
				size = textProvider.getSizeText(((File) item).getSize());
			sb.append(template.sizeCellContent(size));
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
		setupSorting();
		this.listener.onRendered();
	}

	private String getFileStyles(File file, int index) {
		String styles = (index % 2 == 0 ? StyleConstants.FILE_LIST_ROW_FILE_EVEN
				: StyleConstants.FILE_LIST_ROW_FILE_ODD);

		if (file.getExtension().length() > 0)
			styles += " " + StyleConstants.FILE_LIST_FILE_EXTENSION_PREFIX
					+ file.getExtension().toLowerCase();
		else
			styles += " " + StyleConstants.FILE_LIST_FILE_EXTENSION_UNKNOWN;

		return styles;
	}

	private String getFolderStyles(Folder folder, int index) {
		String styles = (index % 2 == 0 ? StyleConstants.FILE_LIST_ROW_DIRECTORY_EVEN
				: StyleConstants.FILE_LIST_ROW_DIRECTORY_ODD);
		if (Folder.Parent.equals(folder))
			styles += " " + StyleConstants.FILE_LIST_ROW_DIRECTORY_PARENT;
		return styles;
	}

	@Override
	public void sortColumn(String columnId, SortOrder sort) {
		// TODO Auto-generated method stub
	}
}
