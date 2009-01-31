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

import org.sjarvela.mollify.client.TextProvider;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.Coords;
import org.sjarvela.mollify.client.ui.DataGrid;
import org.sjarvela.mollify.client.ui.HoverDecorator;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FileList extends DataGrid {
	private List<FileListListener> listeners = new ArrayList<FileListListener>();
	private List<FileSystemItem> content = new ArrayList();
	private Comparator<FileSystemItem> comparator = new DefaultFileItemComparator();
	private TextProvider textProvider;
	private List<String> rowStyles = new ArrayList();

	public FileList(TextProvider textProvider, Localizator localizator) {
		super(StyleConstants.FILE_LIST_HEADER);
		this.textProvider = textProvider;

		// setup header
		// this.setHeaderText(Column.SELECT, localizator.getStrings()
		// .fileListColumnTitleSelect());
		this.setHeaderText(Column.NAME, localizator.getStrings()
				.fileListColumnTitleName());
		this.setHeaderText(Column.TYPE, localizator.getStrings()
				.fileListColumnTitleType());
		this.setHeaderText(Column.SIZE, localizator.getStrings()
				.fileListColumnTitleSize());

		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);

		this.addStyleName(StyleConstants.FILE_LIST);
	}

	private void setHeaderText(Column column, String text) {
		this.setHeaderText(column.ordinal(), text, column.name().toLowerCase());
	}

	public void addListener(FileListListener listener) {
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

	protected void onDirectoryIconClicked(Directory directory) {
		for (FileListListener listener : listeners) {
			listener.onIconClicked(directory);
		}
	}

	private void notifyClickListeners(FileSystemItem item, Column column) {
		for (FileListListener listener : listeners) {
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
			String style = "";
			if (item.isFile())
				style = addFileRow(current, (File) item);
			else
				style = addDirectoryRow(current, (Directory) item);
			rowStyles.add(style);
			current++;
		}

		for (int i = 0, n = Column.values().length; i < n; i++) {
			Column col = Column.values()[i];
			getColumnFormatter().addStyleName(
					i,
					StyleConstants.FILE_LIST_COLUMN_PREFIX
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
		rowStyles.clear();
	}

	private String addFileRow(int index, File file) {
		// setWidget(index, Column.SELECT, createSelectWidget(file));
		setWidget(index, Column.NAME, createNameWidget(index, file));
		setWidget(index, Column.TYPE, createExtensionWidget(file));
		setWidget(index, Column.SIZE, createSizeWidget(file));

		List<String> styles = getFileStyles(index, file);
		for (String style : styles) {
			getRowFormatter().addStyleName(index, style);
		}
		return styles.get(0);
	}

	private String addDirectoryRow(final int row, final Directory directory) {
		FlowPanel panel = new FlowPanel();

		Label icon = new Label();
		icon.setStyleName(StyleConstants.FILE_LIST_ROW_DIRECTORY_ICON);
		HoverDecorator.decorate(icon);
		panel.add(icon);

		icon.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				onDirectoryIconClicked(directory);
			}
		});

		panel.add(createNameWidget(row, directory));

		// setText(index, Column.SELECT.ordinal(), "");
		setWidget(row, Column.NAME, panel);
		setText(row, Column.TYPE.ordinal(), "");
		setHTML(row, Column.SIZE.ordinal(), "");

		List<String> styles = getDirectoryStyles(row);
		for (String style : styles) {
			getRowFormatter().addStyleName(row, style);
		}
		return styles.get(0);
	}

	private List<String> getFileStyles(int index, File file) {
		ArrayList<String> styles = new ArrayList<String>();
		styles.add(index % 2 == 0 ? StyleConstants.FILE_LIST_ROW_FILE_EVEN
				: StyleConstants.FILE_LIST_ROW_FILE_ODD);

		if (file.getExtension().length() > 0)
			styles.add(StyleConstants.FILE_LIST_FILE_EXTENSION_PREFIX
					+ file.getExtension().toLowerCase());
		else
			styles.add(StyleConstants.FILE_LIST_FILE_EXTENSION_UNKNOWN);

		return styles;
	}

	// private Widget createSelectWidget(File file) {
	// CheckBox select = new CheckBox();
	// select.setStyleName(StyleConstants.SIMPLE_FILE_LIST_ITEM_SELECT);
	// return select;
	// }

	private Widget createNameWidget(final int row, FileSystemItem item) {
		Label name = new Label(item.getName());
		name.setStyleName(StyleConstants.FILE_LIST_ITEM_NAME);
		name.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				FileList.this.onClick(row, Column.NAME.ordinal());
			}
		});
		HoverDecorator.decorate(name);
		return name;
	}

	private Widget createExtensionWidget(File file) {
		Label name = new Label(file.getExtension());
		name.setStyleName(StyleConstants.FILE_LIST_ITEM_TYPE);
		return name;
	}

	private Widget createSizeWidget(File file) {
		Label name = new Label(textProvider.getSizeText(file.getSize()));
		name.setStyleName(StyleConstants.FILE_LIST_ITEM_SIZE);
		return name;
	}

	private List<String> getDirectoryStyles(int index) {
		ArrayList<String> styles = new ArrayList<String>();
		styles.add(index % 2 == 0 ? StyleConstants.FILE_LIST_ROW_DIRECTORY_EVEN
				: StyleConstants.FILE_LIST_ROW_DIRECTORY_ODD);
		return styles;
	}

	private void setWidget(int row, Column column, Widget widget) {
		widget.addStyleName(StyleConstants.FILE_LIST_COLUMN_PREFIX
				+ column.name().toLowerCase());
		setWidget(row, column.ordinal(), widget);
	}

	public Widget getWidget(FileSystemItem item, Column column) {
		int row = content.indexOf(item);
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

			this.getRowFormatter().addStyleName(row,
					rowStyles.get(row) + "-" + StyleConstants.HOVER);
			break;
		}

		case Event.ONMOUSEOUT: {
			int row = getEventRowNumber(event);
			if (row < 0)
				return;

			this.getRowFormatter().removeStyleName(row,
					rowStyles.get(row) + "-" + StyleConstants.HOVER);
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
