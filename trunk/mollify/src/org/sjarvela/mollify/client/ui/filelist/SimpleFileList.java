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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.Coords;
import org.sjarvela.mollify.client.ui.DataGrid;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;

public class SimpleFileList extends DataGrid {
	private List<SimpleFileListListener> listeners = new ArrayList<SimpleFileListListener>();
	private Localizator localizator;
	private List<FileSystemItem> content = new ArrayList();
	private Comparator<FileSystemItem> comparator = new DefaultFileItemComparator();

	public SimpleFileList(Localizator localizator) {
		super();
		this.localizator = localizator;

		// setup header
		// this.setHeaderText(Column.SELECT, localizator.getStrings()
		// .fileListColumnTitleSelect());
		this.setHeaderText(Column.NAME, localizator.getStrings()
				.fileListColumnTitleName());
		this.setHeaderText(Column.TYPE, localizator.getStrings()
				.fileListColumnTitleType());
		this.setHeaderText(Column.SIZE, localizator.getStrings()
				.fileListColumnTitleSize());

		this.addTableListener(new TableListener() {
			public void onCellClicked(SourcesTableEvents sender, int row,
					int cell) {
				onClick(row, cell);
			}
		});

		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);

		this.addStyleName(StyleConstants.SIMPLE_FILE_LIST);
	}

	private void setHeaderText(Column column, String text) {
		this.setHeaderText(column.ordinal(), text, column.name().toLowerCase());
	}

	public void addListener(SimpleFileListListener listener) {
		listeners.add(listener);
	}

	public void setContent(List<FileSystemItem> list) {
		this.content = new ArrayList(list);
		Collections.sort(content, comparator);
	}

	private void onClick(int row, int col) {
		Column column = Column.values()[col];
		notifyClickListeners(content.get(row), column);
	}

	private void notifyClickListeners(FileSystemItem item, Column column) {
		for (SimpleFileListListener listener : listeners) {
			listener.onRowClicked(item, column);
		}
	}

	public void removeAllRows() {
		int count = getRowCount();
		for (int i = 0; i < count; i++) {
			removeRow(0);
		}
	}

	public void refresh() {
		removeRows();
		int current = 0;

		for (FileSystemItem item : content) {
			if (item.isFile())
				addFileRow(current, (File) item);
			else
				addDirectoryRow(current, (Directory) item);
			current++;
		}

		for (int i = 0, n = Column.values().length; i < n; i++) {
			Column col = Column.values()[i];
			getColumnFormatter().addStyleName(
					i,
					StyleConstants.SIMPLE_FILE_LIST_COLUMN_PREFIX
							+ col.name().toLowerCase());
		}
	}

	private void removeRows() {
		int count = getRowCount();

		if (getRowCount() > 0) {
			for (int i = 0; i < count; i++) {
				removeRow(0);
			}
		}
	}

	private void addFileRow(int index, File file) {
		// setWidget(index, Column.SELECT, createSelectWidget(file));
		setWidget(index, Column.NAME, createNameWidget(file));
		setWidget(index, Column.TYPE, createExtensionWidget(file));
		setWidget(index, Column.SIZE, createSizeWidget(file));

		List<String> styles = getFileStyles(index, file);
		for (String style : styles) {
			getRowFormatter().addStyleName(index, style);
		}
	}

	private void addDirectoryRow(int index, Directory directory) {
		Label name = new Label(directory.getName());
		name.setStyleName(StyleConstants.SIMPLE_FILE_LIST_ITEM_NAME);

		// setText(index, Column.SELECT.ordinal(), "");
		setWidget(index, Column.NAME, name);
		setText(index, Column.TYPE.ordinal(), "");
		setHTML(index, Column.SIZE.ordinal(), "");

		List<String> styles = getDirectoryStyles(index);
		for (String style : styles) {
			getRowFormatter().addStyleName(index, style);
		}
	}

	private List<String> getFileStyles(int index, File file) {
		ArrayList<String> styles = new ArrayList<String>();
		styles
				.add(index % 2 == 0 ? StyleConstants.SIMPLE_FILE_LIST_ROW_FILE_EVEN
						: StyleConstants.SIMPLE_FILE_LIST_ROW_FILE_ODD);

		if (file.getExtension().length() > 0)
			styles.add(StyleConstants.SIMPLE_FILE_LIST_FILE_EXTENSION_PREFIX
					+ file.getExtension().toLowerCase());
		else
			styles.add(StyleConstants.SIMPLE_FILE_LIST_FILE_EXTENSION_UNKNOWN);

		return styles;
	}

	// private Widget createSelectWidget(File file) {
	// CheckBox select = new CheckBox();
	// select.setStyleName(StyleConstants.SIMPLE_FILE_LIST_ITEM_SELECT);
	// return select;
	// }

	private Widget createNameWidget(File file) {
		Label name = new Label(file.getName());
		name.setStyleName(StyleConstants.SIMPLE_FILE_LIST_ITEM_NAME);
		return name;
	}

	private Widget createExtensionWidget(File file) {
		Label name = new Label(file.getExtension());
		name.setStyleName(StyleConstants.SIMPLE_FILE_LIST_ITEM_TYPE);
		return name;
	}

	private Widget createSizeWidget(File file) {
		Label name = new Label(getSizeText(file));
		name.setStyleName(StyleConstants.SIMPLE_FILE_LIST_ITEM_SIZE);
		return name;
	}

	private List<String> getDirectoryStyles(int index) {
		ArrayList<String> styles = new ArrayList<String>();
		styles
				.add(index % 2 == 0 ? StyleConstants.SIMPLE_FILE_LIST_ROW_DIRECTORY_EVEN
						: StyleConstants.SIMPLE_FILE_LIST_ROW_DIRECTORY_ODD);
		return styles;
	}

	private void setWidget(int row, Column column, Widget widget) {
		widget.addStyleName(StyleConstants.SIMPLE_FILE_LIST_COLUMN_PREFIX
				+ column.name().toLowerCase());
		setWidget(row, column.ordinal(), widget);
	}

	// TODO externalize to a text provider etc
	private String getSizeText(File file) {
		int bytes = file.getSize();

		if (bytes < 1024) {
			return (bytes == 1 ? localizator.getMessages().sizeOneByte()
					: localizator.getMessages().sizeInBytes(bytes));
		}

		if (bytes < 1024 * 1024) {
			double kilobytes = (double) bytes / (double) 1024;
			return (kilobytes == 1 ? localizator.getMessages()
					.sizeOneKilobyte() : localizator.getMessages()
					.sizeInKilobytes(kilobytes));
		}

		double megabytes = (double) bytes / (double) (1024 * 1024);
		return localizator.getMessages().sizeInMegabytes(megabytes);
	}

	public Widget getWidget(File file, Column column) {
		int row = content.indexOf(file);
		return this.getWidget(row, column.ordinal());
	}

	public Coords getWidgetCoords(File file, Column column) {
		Widget cell = getWidget(file, column);

		return new Coords(cell.getAbsoluteLeft(), cell.getAbsoluteTop(), cell
				.getOffsetWidth(), cell.getOffsetHeight());
	}

	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEOVER: {
			int row = getEventRowNumber(event);
			if (row < 0)
				return;

			this.getRowFormatter().addStyleName(row, StyleConstants.MOUSE_OVER);

			break;
		}

		case Event.ONMOUSEOUT: {
			int row = getEventRowNumber(event);
			if (row < 0)
				return;

			this.getRowFormatter().removeStyleName(row,
					StyleConstants.MOUSE_OVER);

			break;
		}
		}

		super.onBrowserEvent(event);
	}

	private int getEventRowNumber(Event event) {
		Element cell = getEventTargetCell(event);
		if (cell == null)
			return -1;

		Element row = DOM.getParent(cell);
		if (row == null)
			return -1;
		return DOM.getChildIndex(getBodyElement(), row);
	}
}
