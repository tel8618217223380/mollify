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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.Grid;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.GridDataProvider;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FileList extends Grid<FileSystemItem> implements
		GridDataProvider<FileSystemItem> {
	private final TextProvider textProvider;
	private final DragAndDropManager dragAndDropManager;

	public static GridColumn COLUMN_NAME;
	public static GridColumn COLUMN_TYPE;
	public static GridColumn COLUMN_SIZE;
	public static List<GridColumn> ALL_COLUMNS = null;

	private static String TYPE_DIR = "";
	private static String SIZE_DIR = "";

	public FileList(TextProvider textProvider,
			DragAndDropManager dragAndDropManager) {
		super(StyleConstants.FILE_LIST_HEADER, getColumns(textProvider));

		setDataProvider(this);
		setCustomSelection(true);

		this.textProvider = textProvider;
		this.dragAndDropManager = dragAndDropManager;
		this.addStyleName(StyleConstants.FILE_LIST);
	}

	private static List<GridColumn> getColumns(TextProvider textProvider) {
		if (ALL_COLUMNS == null) {
			COLUMN_NAME = new DefaultGridColumn("name", textProvider
					.getStrings().fileListColumnTitleName(), true);
			COLUMN_TYPE = new DefaultGridColumn("type", textProvider
					.getStrings().fileListColumnTitleType(), true);
			COLUMN_SIZE = new DefaultGridColumn("size", textProvider
					.getStrings().fileListColumnTitleSize(), true);

			ALL_COLUMNS = Arrays.asList((GridColumn) COLUMN_NAME,
					(GridColumn) COLUMN_TYPE, (GridColumn) COLUMN_SIZE);

			TYPE_DIR = textProvider.getStrings().fileListDirectoryType();
		}

		return ALL_COLUMNS;
	}

	public GridData getData(FileSystemItem item, GridColumn column) {
		if (item.isFile())
			return getFileData((File) item, column);
		return getDirectoryData((Folder) item, column);
	}

	private GridData getFileData(File file, GridColumn column) {
		if (column.equals(COLUMN_NAME))
			return new GridData.Widget(createFileNameWidget(file));
		else if (column.equals(COLUMN_TYPE))
			return new GridData.Widget(createTypeWidget(file));
		else if (column.equals(COLUMN_SIZE))
			return new GridData.Widget(createSizeWidget(file));
		return new GridData.Text("");
	}

	private GridData getDirectoryData(Folder directory, GridColumn column) {
		if (column.equals(COLUMN_NAME))
			return new GridData.Widget(createDirectoryNameWidget(directory));
		else if (column.equals(COLUMN_TYPE))
			return new GridData.Widget(createTypeWidget(directory));
		else if (column.equals(COLUMN_SIZE))
			return new GridData.Text(SIZE_DIR);
		return new GridData.Text("");
	}

	private FlowPanel createDirectoryNameWidget(final Folder folder) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.FILE_LIST_ITEM_NAME_PANEL);
		panel.add(createSelector(folder));

		Label icon = new Label();
		icon.setStyleName(StyleConstants.FILE_LIST_ROW_DIRECTORY_ICON);
		HoverDecorator.decorate(icon);
		panel.add(icon);

		icon.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FileList.this.onIconClicked(folder);
			}
		});

		panel.add(createNameWidget(folder, !folder.equals(Folder.Parent)));
		return panel;
	}

	private FlowPanel createFileNameWidget(final File file) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.FILE_LIST_ITEM_NAME_PANEL);
		panel.add(createSelector(file));

		Label icon = new Label();
		icon.setStyleName(StyleConstants.FILE_LIST_ROW_FILE_ICON);
		HoverDecorator.decorate(icon);
		panel.add(icon);
		panel.add(createNameWidget(file, true));
		return panel;
	}

	private Widget createSelector(final FileSystemItem item) {
		Label selector = new Label();
		selector.setStyleName(StyleConstants.FILE_LIST_ROW_SELECTOR);
		selector.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FileList.this.onRowSelectorClicked(item);
			}
		});
		HoverDecorator.decorate(selector);
		return selector;
	}

	protected void onRowSelectorClicked(FileSystemItem item) {
		updateSelection(item);
	}

	private Widget createNameWidget(final FileSystemItem item, boolean draggable) {
		DraggableFileSystemItem itemWidget = new DraggableFileSystemItem(item);
		itemWidget.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FileList.this.onClick(item, COLUMN_NAME);
			}
		});
		if (draggable)
			dragAndDropManager.getController(FileSystemItem.class)
					.makeDraggable(itemWidget);
		return itemWidget;
	}

	private Widget createTypeWidget(FileSystemItem item) {
		Label name = new Label(item.isFile() ? ((File) item).getExtension()
				: TYPE_DIR);
		name.setStyleName(StyleConstants.FILE_LIST_ITEM_TYPE);
		return name;
	}

	private Widget createSizeWidget(File file) {
		Label name = new Label(textProvider.getSizeText(file.getSize()));
		name.setStyleName(StyleConstants.FILE_LIST_ITEM_SIZE);
		return name;
	}

	public List<String> getRowStyles(FileSystemItem t) {
		if (t.isFile())
			return getFileStyles((File) t);
		return getDirectoryStyles((Folder) t);
	}

	private List<String> getFileStyles(File file) {
		ArrayList<String> styles = new ArrayList<String>();
		int index = getRowIndex(file);

		styles.add(index % 2 == 0 ? StyleConstants.FILE_LIST_ROW_FILE_EVEN
				: StyleConstants.FILE_LIST_ROW_FILE_ODD);

		if (file.getExtension().length() > 0)
			styles.add(StyleConstants.FILE_LIST_FILE_EXTENSION_PREFIX
					+ file.getExtension().toLowerCase());
		else
			styles.add(StyleConstants.FILE_LIST_FILE_EXTENSION_UNKNOWN);

		return styles;
	}

	private List<String> getDirectoryStyles(Folder directory) {
		ArrayList<String> styles = new ArrayList<String>();
		int index = getRowIndex(directory);

		styles.add(index % 2 == 0 ? StyleConstants.FILE_LIST_ROW_DIRECTORY_EVEN
				: StyleConstants.FILE_LIST_ROW_DIRECTORY_ODD);
		return styles;
	}

	public String getColumnStyle(GridColumn column) {
		return StyleConstants.FILE_LIST_COLUMN_PREFIX + column.getId();
	}
}