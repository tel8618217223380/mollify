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

import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.formatter.PathFormatter;

public class SearchResultFileList extends FileList {
	private static final String COLUMN_ID_PATH = "path";

	private final PathFormatter formatter;

	public SearchResultFileList(TextProvider textProvider,
			PathFormatter formatter) {
		super(textProvider, null);
		this.formatter = formatter;
		setSelectionMode(SelectionMode.Multi);
	}

	protected List<GridColumn> getColumns() {
		GridColumn columnName = new DefaultGridColumn(COLUMN_ID_NAME,
				textProvider.getStrings().fileListColumnTitleName(), true);
		GridColumn columnPath = new DefaultGridColumn(COLUMN_ID_PATH,
				"TODO path", true);
		GridColumn columnSize = new DefaultGridColumn(COLUMN_ID_SIZE,
				textProvider.getStrings().fileListColumnTitleSize(), true);

		return Arrays.asList((GridColumn) columnName, (GridColumn) columnPath,
				(GridColumn) columnSize);
	}

	public GridData getData(FileSystemItem item, GridColumn column) {
		if (column.getId().equals(COLUMN_ID_PATH))
			return new GridData.Text(formatter.format(item));
		return super.getData(item, column);
	}
}
