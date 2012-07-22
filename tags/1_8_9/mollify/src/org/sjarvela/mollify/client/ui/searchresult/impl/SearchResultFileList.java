/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.searchresult.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.SearchMatch;
import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.common.HtmlTooltip;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.formatter.PathFormatter;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.FlowPanel;

public class SearchResultFileList extends FileList {
	public static final String COLUMN_ID_PATH = "path";

	private final PathFormatter formatter;

	private SearchResult result;

	public SearchResultFileList(TextProvider textProvider,
			PathFormatter formatter) {
		super(textProvider, null);
		this.formatter = formatter;
		setSelectionMode(SelectionMode.Multi);
	}

	protected List<GridColumn> initColumns() {
		GridColumn columnName = new DefaultGridColumn(COLUMN_ID_NAME,
				textProvider.getText(Texts.fileListColumnTitleName), true);
		GridColumn columnPath = new DefaultGridColumn(COLUMN_ID_PATH,
				textProvider.getText(Texts.searchResultListColumnTitlePath),
				true);
		GridColumn columnSize = new DefaultGridColumn(COLUMN_ID_SIZE,
				textProvider.getText(Texts.fileListColumnTitleSize), true);

		return Arrays.asList((GridColumn) columnName, (GridColumn) columnPath,
				(GridColumn) columnSize);
	}

	public GridData getData(FileSystemItem item, GridColumn column) {
		if (column.getId().equals(COLUMN_ID_PATH))
			return new GridData.Text(formatter.format(item));
		return super.getData(item, column);
	}

	public void setResults(SearchResult result) {
		this.result = result;
		setContent(getItems());
	}

	private List<FileSystemItem> getItems() {
		List<FileSystemItem> list = new ArrayList();
		List<String> matchKeys = result.getMatches();
		for (String id : matchKeys) {
			SearchMatch m = result.getMatch(id);
			list.add(m.getItem());
		}
		return list;
	}

	@Override
	protected FlowPanel createFileNameWidget(File file) {
		FlowPanel w = super.createFileNameWidget(file);
		addMatchTooltip(w, file);
		return w;
	}

	@Override
	protected FlowPanel createFolderNameWidget(Folder folder) {
		FlowPanel w = super.createFolderNameWidget(folder);
		addMatchTooltip(w, folder);
		return w;
	}

	private void addMatchTooltip(FlowPanel w, FileSystemItem item) {
		SearchMatch match = result.getMatch(item.getId());
		JsArray matches = match.getMatches();

		String html = "<span class='title'>"
				+ textProvider.getText(Texts.searchResultsTooltipMatches)
				+ "</span><ul>";
		for (int i = 0; i < matches.length(); i++)
			html += addMatch(matches.get(i).<JsObj> cast());
		html += "</ul>";
		new HtmlTooltip("search-results", html).attachTo(w);
	}

	private String addMatch(JsObj m) {
		String type = m.getString("type");
		String html = type;
		if ("name".equals(type)) {
			html = "<span class='title'>"
					+ textProvider.getText(Texts.searchResultsTooltipMatchName)
					+ "</span>";
		} else if ("description".equals(type)) {
			html = "<span class='title'>"
					+ textProvider
							.getText(Texts.searchResultsTooltipMatchDescription)
					+ ":</span>&nbsp;" + m.getString("description");
		}
		return "<li>" + html + "</li>";
	}
}
