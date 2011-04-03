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
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.Grid;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.GridDataProvider;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FileList extends Grid<FileSystemItem> implements
		GridDataProvider<FileSystemItem> {
	public static String COLUMN_ID_NAME = "name";
	public static String COLUMN_ID_TYPE = "type";
	public static String COLUMN_ID_SIZE = "size";

	private final DragAndDropManager dragAndDropManager;

	private String typeTextFolder = "";
	private String sizeTextFolder = "";

	public FileList(TextProvider textProvider,
			DragAndDropManager dragAndDropManager) {
		super(textProvider, StyleConstants.FILE_LIST_HEADER);

		typeTextFolder = textProvider.getText(Texts.fileListDirectoryType);

		setDataProvider(this);
		setCustomSelection(true);

		this.dragAndDropManager = dragAndDropManager;
		this.addStyleName(StyleConstants.FILE_LIST);
	}

	protected List<GridColumn> getColumns() {
		GridColumn columnName = new DefaultGridColumn(COLUMN_ID_NAME,
				textProvider.getText(Texts.fileListColumnTitleName), true);
		GridColumn columnType = new DefaultGridColumn(COLUMN_ID_TYPE,
				textProvider.getText(Texts.fileListColumnTitleType), true);
		GridColumn columnSize = new DefaultGridColumn(COLUMN_ID_SIZE,
				textProvider.getText(Texts.fileListColumnTitleSize), true);

		return Arrays.asList((GridColumn) columnName, (GridColumn) columnType,
				(GridColumn) columnSize);
	}

	public GridData getData(FileSystemItem item, GridColumn column) {
		if (item.isFile())
			return getFileData((File) item, column);
		return getFolderData((Folder) item, column);
	}

	private GridData getFileData(File file, GridColumn column) {
		if (column.getId().equals(COLUMN_ID_NAME))
			return new GridData.Widget(createFileNameWidget(file));
		else if (column.getId().equals(COLUMN_ID_TYPE))
			return new GridData.Widget(createTypeWidget(file));
		else if (column.getId().equals(COLUMN_ID_SIZE))
			return new GridData.Widget(createSizeWidget(file));
		return new GridData.Text("");
	}

	private GridData getFolderData(Folder folder, GridColumn column) {
		if (column.getId().equals(COLUMN_ID_NAME))
			return new GridData.Widget(createFolderNameWidget(folder));
		else if (column.getId().equals(COLUMN_ID_TYPE))
			return new GridData.Widget(createTypeWidget(folder));
		else if (column.getId().equals(COLUMN_ID_SIZE))
			return new GridData.Text(sizeTextFolder);
		return new GridData.Text("");
	}

	private FlowPanel createFolderNameWidget(final Folder folder) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.FILE_LIST_ITEM_NAME_PANEL);
		panel.add(createSelector(folder));

		final Label icon = new Label();
		icon.setStyleName(StyleConstants.FILE_LIST_ROW_DIRECTORY_ICON);
		HoverDecorator.decorate(icon);
		panel.add(icon);

		final Widget nameWidget = createNameWidget(folder,
				!folder.equals(Folder.Parent));
		icon.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FileList.this.onIconClicked(folder,
						getWidget(folder, COLUMN_ID_NAME).getElement());
			}
		});

		panel.add(nameWidget);
		return panel;
	}

	private FlowPanel createFileNameWidget(final File file) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.FILE_LIST_ITEM_NAME_PANEL);
		panel.add(createSelector(file));

		final Label icon = new Label();
		icon.setStyleName(StyleConstants.FILE_LIST_ROW_FILE_ICON);
		HoverDecorator.decorate(icon);

		final Widget nameWidget = createNameWidget(file, true);
		icon.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FileList.this.onIconClicked(file,
						getWidget(file, COLUMN_ID_NAME).getElement());
			}
		});

		panel.add(icon);
		panel.add(nameWidget);
		return panel;
	}

	public void onIconClicked(FileSystemItem item, Element e) {
		for (GridListener listener : listeners)
			listener.onIconClicked(item, e);
	}

	private Widget createSelector(final FileSystemItem item) {
		Label selector = new Label();
		if (Folder.Parent.equals(item)
				|| (!item.isFile() && ((Folder) item).isRoot())) {
			selector.setStyleName(StyleConstants.FILE_LIST_ROW_EMPTY_SELECTOR);
		} else {
			selector.setStyleName(StyleConstants.FILE_LIST_ROW_SELECTOR);

			selector.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					FileList.this.onRowSelectorClicked(item);
				}
			});
			HoverDecorator.decorate(selector);
		}
		return selector;
	}

	protected void onRowSelectorClicked(FileSystemItem item) {
		if (isSelectable(item))
			updateSelection(item);
	}

	private Widget createNameWidget(final FileSystemItem item, boolean draggable) {
		DraggableFileSystemItem itemWidget = new DraggableFileSystemItem(item);
		itemWidget.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FileList.this.onClick(item, COLUMN_ID_NAME);
			}
		});
		if (draggable && dragAndDropManager != null)
			dragAndDropManager.getController(FileSystemItem.class)
					.makeDraggable(itemWidget);
		return itemWidget;
	}

	private Widget createTypeWidget(FileSystemItem item) {
		Label name = new Label(item.isFile() ? ((File) item).getExtension()
				: (Folder.Parent.equals(item) ? "" : typeTextFolder));
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
		return getFolderStyles((Folder) t);
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

	private List<String> getFolderStyles(Folder folder) {
		ArrayList<String> styles = new ArrayList<String>();
		int index = getRowIndex(folder);

		styles.add(index % 2 == 0 ? StyleConstants.FILE_LIST_ROW_DIRECTORY_EVEN
				: StyleConstants.FILE_LIST_ROW_DIRECTORY_ODD);
		if (Folder.Parent.equals(folder))
			styles.add(StyleConstants.FILE_LIST_ROW_DIRECTORY_PARENT);
		return styles;
	}

	public String getColumnStyle(GridColumn column) {
		return StyleConstants.FILE_LIST_COLUMN_PREFIX + column.getId();
	}
}
