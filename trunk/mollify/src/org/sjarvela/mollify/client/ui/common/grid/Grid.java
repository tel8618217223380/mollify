/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.Coords;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Grid<T> extends FlexTable {
	private static final String DEFAULT_ROW_STYLE = "grid-row";
	private static final String TITLE_STYLE = "-title";
	private static final String SORT_STYLE = "-sort";

	protected final TextProvider textProvider;
	private final String headerCss;
	private final Map<GridColumn, GridColumnSortButton> sortButtons = new HashMap();
	protected final List<GridListener> listeners = new ArrayList<GridListener>();
	private List<GridColumn> columns = Collections.EMPTY_LIST;

	private final String sortableHeaderTitleCss;
	private final String sortableHeaderSortCss;
	private final List<String> rowStyles = new ArrayList();

	private Element head;
	private Element headerRow;

	private List<T> content = new ArrayList();
	private List<T> selected = new ArrayList();
	private SelectionMode selectionMode = SelectionMode.None;
	private boolean customSelection = false;
	private GridDataProvider<T> dataProvider = null;
	private GridComparator<T> comparator = null;
	private Map<Element, Widget> eventWidgets = new HashMap();
	private Map<Class, String> widgetBaseClasses = new HashMap();
	private SelectController selectController;

	public Grid(TextProvider textProvider, String headerCss) {
		super();
		this.textProvider = textProvider;

		this.headerCss = headerCss;
		this.sortableHeaderTitleCss = headerCss + TITLE_STYLE;
		this.sortableHeaderSortCss = headerCss + SORT_STYLE;

		sinkEvents(Event.ONCLICK);
		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);

		initialize();
	}

	protected void initialize() {
		initializeElement();
		initializeColumns();
	}

	protected abstract List<GridColumn> initColumns();

	private void initializeElement() {
		head = DOM.createTHead();
		headerRow = DOM.createTR();

		DOM.insertChild(getElement(), head, 0);
		DOM.insertChild(head, headerRow, 0);
		DOM.setElementAttribute(getBodyElement(), "style",
				"overflow:auto;text-align: left;");
		DOM.setElementAttribute(head, "style", "text-align: left;");

		widgetBaseClasses.put(GridColumnHeaderTitle.class,
				sortableHeaderTitleCss);
		widgetBaseClasses
				.put(GridColumnSortButton.class, sortableHeaderSortCss);
	}

	private void initializeColumns() {
		this.columns = initColumns();
		addHeaderCells(headerRow, columns.size());

		int index = 0;
		for (GridColumn column : columns) {
			initializeColumn(index, column);
			index++;
		}
	}

	public List<GridColumn> getColumns() {
		return columns;
	}

	private native void addHeaderCells(Element row, int count) /*-{
		for ( var i = 0; i < count; i++) {
			var cell = $doc.createElement("th");
			row.appendChild(cell);
		}
	}-*/;

	private void initializeColumn(int index, GridColumn column) {
		Element th = DOM.getChild(headerRow, index);
		th.setAttribute("class", headerCss + "-th");
		th.setId(headerCss + "-th-" + column.getId());

		Widget headerElement = createHeaderWidget(column);
		headerElement.setStyleName(headerCss);
		headerElement.getElement().setId(headerCss + "-" + column.getId());

		th.appendChild(headerElement.getElement());
	}

	private Widget createHeaderWidget(final GridColumn column) {
		if (!column.isSortable())
			return new Label(column.getTitle());

		Panel panel = new FlowPanel();
		panel.add(createSortableTitle(column));
		panel.add(createSortButton(column));
		return panel;
	}

	private Label createSortableTitle(final GridColumn column) {
		GridColumnHeaderTitle title = new GridColumnHeaderTitle(column,
				sortableHeaderTitleCss);
		addSortClickListener(this, title.getElement(), column);
		eventWidgets.put(title.getElement(), title);
		return title;
	}

	private GridColumnSortButton createSortButton(final GridColumn column) {
		final GridColumnSortButton sortButton = new GridColumnSortButton(
				column, sortableHeaderSortCss);
		addSortClickListener(this, sortButton.getElement(), column);
		sortButtons.put(column, sortButton);
		eventWidgets.put(sortButton.getElement(), sortButton);
		return sortButton;
	}

	private native void addSortClickListener(Grid grid, Element element,
			GridColumn column) /*-{
		element.onclick = function() {
			grid.@org.sjarvela.mollify.client.ui.common.grid.Grid::onColumnSortClick(Lorg/sjarvela/mollify/client/ui/common/grid/GridColumn;)(column);
		};
	}-*/;

	protected void onColumnSortClick(GridColumn column) {
		SortOrder sort = SortOrder.none;

		if (this.comparator != null)
			if (this.comparator.getColumnId().equals(column.getId()))
				sort = toggleSort(this.comparator.getSort());
			else
				sort = SortOrder.asc;

		for (GridListener listener : listeners)
			listener.onColumnSorted(column.getId(), sort);
	}

	public void setSelectionMode(SelectionMode mode) {
		selectionMode = mode;
		removeAllSelectionModeStyles();
		if (!SelectionMode.None.equals(mode))
			this.addStyleDependentName(mode.name().toLowerCase());
		removeAllSelections();
	}

	public void setSelectController(SelectController controller) {
		this.selectController = controller;
	}

	private void removeAllSelectionModeStyles() {
		this.removeStyleDependentName("multi");
		this.removeStyleDependentName("single");
	}

	public List<T> getSelected() {
		return selected;
	}

	public void selectAll() {
		for (T t : content)
			if (isSelectable(t) && !selected.contains(t)) {
				selected.add(t);
				addSelectedStyle(t);
			}
		notifySelectionChange();
	}

	protected boolean isSelectable(T t) {
		return selectController == null || selectController.isSelectable(t);
	}

	public void selectNone() {
		removeAllSelections();
		notifySelectionChange();
	}

	private SortOrder toggleSort(SortOrder sort) {
		if (SortOrder.asc.equals(sort))
			return SortOrder.desc;
		return SortOrder.asc;
	}

	public int getColumnIndex(GridColumn column) {
		return columns.indexOf(column);
	}

	public void addListener(GridListener listener) {
		listeners.add(listener);
	}

	public GridDataProvider getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(GridDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void setComparator(GridComparator<T> comparator) {
		this.comparator = comparator;
		if (dataProvider == null)
			return;
		sort();
	}

	public void setContent(List<T> list) {
		if (dataProvider == null)
			throw new RuntimeException("No data provider");

		clearSelection();
		this.content = new ArrayList(list);
		sort();
	}

	private void clearSelection() {
		if (selected.size() > 0)
			selected.clear();
		notifySelectionChange();
	}

	private void sort() {
		if (comparator != null)
			Collections.sort(content, comparator);

		for (GridColumn column : columns) {
			if (sortButtons.containsKey(column)) {
				SortOrder sort = comparator == null ? SortOrder.none : (column
						.getId().equals(comparator.getColumnId()) ? comparator
						.getSort() : SortOrder.none);
				sortButtons.get(column).setSort(sort);
			}
		}
		refresh();
	}

	public void refresh() {
		removeAllRows();

		final Iterator<T> iterator = this.content.iterator();
		RepeatingCommand cmd = new RepeatingCommand() {
			int row = 0;

			public boolean execute() {
				int processed = process(row, iterator);
				if (processed == 0) {
					onRenderFinished();

					return false;
				}
				row += processed;
				return true;
			}
		};

		Scheduler.get().scheduleIncremental(cmd);
	}

	protected int process(int start, Iterator<T> iterator) {
		if (!iterator.hasNext())
			return 0;

		int processed = 0;
		int row = start;
		while (true) {
			T t = iterator.next();
			add(row, t);
			row++;
			processed++;

			if (!iterator.hasNext())
				return 0;
			if (processed == 100)
				break;
		}
		return processed;
	}

	private void add(int row, T t) {
		int column = 0;

		for (GridColumn col : columns) {
			dataProvider.getData(t, col).applyTo(row, column, this);
			String columnStyle = dataProvider.getColumnStyle(col);
			if (columnStyle != null)
				getCellFormatter().addStyleName(row, column, columnStyle);
			column++;
		}

		List<String> styles = dataProvider.getRowStyles(t);

		for (String style : styles)
			getRowFormatter().addStyleName(row, style);

		if (styles.size() > 0)
			rowStyles.add(styles.get(0));
		else
			rowStyles.add(DEFAULT_ROW_STYLE);

		if (selected.contains(t))
			addSelectedStyle(t);
	}

	private void onRenderFinished() {
		for (GridListener listener : listeners)
			listener.onRendered();
	}

	public int getRowIndex(T t) {
		return content.indexOf(t);
	}

	protected void onClick(int row, GridColumn column) {
		onClick(content.get(row), column.getId());
	}

	protected void onClick(T t, String columnId) {
		for (GridListener listener : listeners)
			listener.onColumnClicked(t, columnId, getWidget(t, columnId)
					.getElement());
	}

	private void removeAllSelections() {
		for (T t : selected)
			removeSelectedStyle(t);
		clearSelection();
	}

	private void addSelectedStyle(T t) {
		int row = content.indexOf(t);
		getRowFormatter().addStyleName(row,
				rowStyles.get(row) + "-" + StyleConstants.SELECTED);
	}

	private void removeSelectedStyle(T t) {
		int row = content.indexOf(t);
		getRowFormatter().removeStyleName(row,
				rowStyles.get(row) + "-" + StyleConstants.SELECTED);
	}

	public void removeAllRows() {
		int count = getRowCount();
		if (count > 0) {
			for (int i = 0; i < count; i++)
				removeRow(0);
		}
		rowStyles.clear();
	}

	@Override
	public void onBrowserEvent(Event event) {
		Widget w = getWidget(event);
		if (w != null)
			onWidgetEvent(w, event);
		else
			onRowEvent(event);

		super.onBrowserEvent(event);
	}

	private Widget getWidget(Event event) {
		Element e = DOM.eventGetTarget(event);
		if (!eventWidgets.containsKey(e))
			return null;
		return eventWidgets.get(e);
	}

	private void onWidgetEvent(Widget w, Event event) {
		String baseClass = widgetBaseClasses.get(w.getClass());

		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEOVER:
			w.addStyleName(baseClass + "-" + StyleConstants.HOVER);
			break;

		case Event.ONMOUSEOUT:
			w.removeStyleName(baseClass + "-" + StyleConstants.HOVER);
			break;
		}
	}

	private void onRowEvent(Event event) {
		int row = getEventRowNumber(event);
		if (row < 0)
			return;

		switch (DOM.eventGetType(event)) {
		case Event.ONCLICK:
			if (!customSelection && !selectionMode.equals(SelectionMode.None))
				updateSelection(row);
			break;

		case Event.ONMOUSEOVER:
			this.getRowFormatter().addStyleName(row,
					rowStyles.get(row) + "-" + StyleConstants.HOVER);
			break;

		case Event.ONMOUSEOUT:
			this.getRowFormatter().removeStyleName(row,
					rowStyles.get(row) + "-" + StyleConstants.HOVER);
			break;
		}
	}

	protected void setCustomSelection(boolean b) {
		this.customSelection = b;
	}

	protected void updateSelection(T t) {
		int row = content.indexOf(t);
		updateSelection(row);
	}

	protected void updateSelection(int row) {
		T t = content.get(row);
		boolean previouslySelected = selected.contains(t);

		if (selectionMode.equals(SelectionMode.Single)) {
			removeAllSelections();
			selected.add(t);
			addSelectedStyle(t);
		} else if (selectionMode.equals(SelectionMode.Multi)) {
			if (previouslySelected) {
				selected.remove(t);
				removeSelectedStyle(t);
			} else {
				selected.add(t);
				addSelectedStyle(t);
			}
		}
		notifySelectionChange();
	}

	private void notifySelectionChange() {
		for (GridListener listener : listeners)
			listener.onSelectionChanged(selected);
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

	public Widget getWidget(T t, String columnId) {
		return getWidget(t, getColumn(columnId));
	}

	private Widget getWidget(T t, GridColumn column) {
		return getWidget(content.indexOf(t), getColumnIndex(column));
	}

	private GridColumn getColumn(String columnId) {
		for (GridColumn column : columns)
			if (column.getId().equals(columnId))
				return column;
		return null;
	}

	public Coords getWidgetCoords(T t, GridColumn column) {
		Widget cell = getWidget(t, column);
		return new Coords(cell.getAbsoluteLeft(), cell.getAbsoluteTop(),
				cell.getOffsetWidth(), cell.getOffsetHeight());
	}
}