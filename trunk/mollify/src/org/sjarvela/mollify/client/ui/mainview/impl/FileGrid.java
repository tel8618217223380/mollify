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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectController;
import org.sjarvela.mollify.client.ui.filelist.FileList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class FileGrid extends Composite {
	private final boolean thumbnails;
	private final FileSystemService service;

	private Panel panel;
	private List<FileSystemItem> items = Collections.EMPTY_LIST;
	private Map<FileSystemItem, GridFileWidget> widgets = new HashMap();
	private GridComparator<FileSystemItem> comparator;
	private List<GridListener> listeners = new ArrayList();
	private List<FileSystemItem> selected = new ArrayList();
	private FileSystemItem hilighted = null;
	private Timer clickTimer;
	private SelectController selectController;
	private boolean selectMode = false;

	public FileGrid(boolean thumbnails, FileSystemService service) {
		this.thumbnails = thumbnails;
		this.service = service;

		panel = createWidget();
		initWidget(panel);
	}

	private Panel createWidget() {
		Panel panel = new FlowPanel();
		panel.setStylePrimaryName("mollify-file-grid");
		return panel;
	}

	public void setContent(List<FileSystemItem> items) {
		this.items = items;
		sort();
		refresh();
	}

	public void setComparator(GridComparator<FileSystemItem> comparator) {
		this.comparator = comparator;
		sort();
		refresh();
	}

	private void sort() {
		if (comparator == null)
			return;
		List<FileSystemItem> list = new ArrayList(items);
		Collections.sort(list, comparator);
		this.items = list;
	}

	public void refresh() {
		panel.clear();
		widgets.clear();
		selected.clear();
		hilighted = null;

		for (final FileSystemItem item : this.items) {
			GridFileWidget widget = createItemWidget(item);
			panel.add(widget);
			widgets.put(item, widget);
		}
		onSelectionChanged();
	}

	private GridFileWidget createItemWidget(final FileSystemItem item) {
		GridFileWidget widget = new GridFileWidget(item, thumbnails, service);
		widget.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onItemClicked(item);
			}
		}, ClickEvent.getType());
		widget.addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				onItemDblClicked(item);
			}
		}, DoubleClickEvent.getType());
		return widget;
	}

	protected void onItemClicked(final FileSystemItem item) {
		if (clickTimer != null) {
			clickTimer.cancel();
			clickTimer = null;
		}
		clickTimer = new Timer() {
			public void run() {
				onClick(item);
			}
		};

		clickTimer.schedule(300);
	}

	protected void onClick(FileSystemItem item) {
		changeHilight(item);
		updateSelectionOnClick(item);

		if (!selectMode)
			for (GridListener l : listeners)
				l.onIconClicked(item);

		onSelectionChanged();
	}

	private void updateSelectionOnClick(FileSystemItem item) {
		if (!selectMode)
			clearSelected();
		if (!canSelect(item))
			return;

		GridFileWidget widget = widgets.get(item);
		if (selectMode) {
			boolean isSelected = selected.contains(item);
			widget.select(!isSelected);
			if (isSelected)
				selected.remove(item);
			else
				selected.add(item);
		} else {
			widget.select(true);
			selected.add(item);
		}
	}

	private void changeHilight(FileSystemItem item) {
		if (hilighted != null)
			widgets.get(hilighted).hilight(false);
		if (selectMode)
			return;
		hilighted = item;
		widgets.get(hilighted).hilight(true);
	}

	private boolean canSelect(FileSystemItem item) {
		if (selectController != null)
			return selectController.isSelectable(item);
		return true;
	}

	private void onSelectionChanged() {
		for (GridListener l : listeners) {
			l.onSelectionChanged(selected);
		}
	}

	protected void onItemDblClicked(FileSystemItem item) {
		if (clickTimer != null) {
			clickTimer.cancel();
			clickTimer = null;
		}

		for (GridListener l : listeners)
			l.onColumnClicked(item, FileList.COLUMN_ID_NAME);
	}

	public Widget getWidget(FileSystemItem item) {
		return widgets.get(item);
	}

	public void addListener(GridListener listener) {
		this.listeners.add(listener);
	}

	public void selectAll() {
		clearSelected();
		for (final FileSystemItem item : this.items) {
			selected.add(item);
			widgets.get(item).select(true);
		}
		onSelectionChanged();
	}

	public void selectNone() {
		clearSelected();
		onSelectionChanged();
	}

	private void clearSelected() {
		for (final FileSystemItem item : selected)
			widgets.get(item).select(false);
		selected.clear();
	}

	private void clearHilight() {
		if (hilighted != null) {
			widgets.get(hilighted).hilight(false);
			hilighted = null;
		}
	}

	public void clear() {
		clearSelected();
		panel.clear();
		widgets.clear();
		items.clear();
		hilighted = null;
	}

	public void setSelectMode(boolean b) {
		this.selectMode = b;
		if (selectMode)
			clearHilight();
	}

	public void setSelectController(SelectController selectController) {
		this.selectController = selectController;
	}
}
