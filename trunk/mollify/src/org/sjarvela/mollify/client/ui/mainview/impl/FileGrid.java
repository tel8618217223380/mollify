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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
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
	private Panel panel;
	private List<FileSystemItem> items = Collections.EMPTY_LIST;
	private Map<FileSystemItem, GridFileWidget> widgets = new HashMap();
	private GridComparator<FileSystemItem> comparator;
	private List<GridListener> listeners = new ArrayList();
	private FileSystemItem selected = null;
	private Timer clickTimer;

	private final boolean thumbnails;
	private final FileSystemService service;

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
		selected = null;

		for (final FileSystemItem item : this.items) {
			GridFileWidget widget = createItemWidget(item);
			panel.add(widget);
			widgets.put(item, widget);
		}
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

		clickTimer.schedule(100);
	}

	protected void onClick(FileSystemItem item) {
		if (selected != null)
			widgets.get(selected).select(false);
		selected = item;
		widgets.get(selected).select(true);

		for (GridListener l : listeners) {
			l.onIconClicked(item);
			l.onSelectionChanged(Arrays.asList(item));
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

}
