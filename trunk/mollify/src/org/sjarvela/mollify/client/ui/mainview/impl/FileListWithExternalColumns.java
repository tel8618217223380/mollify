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
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.plugin.ClientInterface;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.filelist.DefaultFileItemComparator;
import org.sjarvela.mollify.client.ui.filelist.FileList;

import com.google.gwt.user.client.ui.Widget;

public class FileListWithExternalColumns extends FileList implements FileWidget {
	private final ClientInterface pluginEnv;
	private final JsObj columnSetup;

	private JsObj data = null;

	public FileListWithExternalColumns(TextProvider textProvider,
			DragAndDropManager dragAndDropManager,
			final ClientInterface pluginEnvironment, final JsObj columnSetup) {
		super(textProvider, dragAndDropManager);
		this.pluginEnv = pluginEnvironment;
		this.columnSetup = columnSetup;
		super.initialize();
	}

	@Override
	protected void initialize() {
		// prevent initialization before own constructor has finished
	}

	@Override
	protected List<GridColumn> initColumns() {
		if (columnSetup == null || columnSetup.getKeys().size() == 0)
			return super.initColumns();

		List<GridColumn> c = new ArrayList();
		for (String id : columnSetup.getKeys()) {
			if (id == null || id.startsWith("_"))
				continue;

			JsObj col = columnSetup.getJsObj(id);
			String titleKey = col.getString("title");

			GridColumn column = null;
			if (COLUMN_ID_NAME.equals(id))
				column = new DefaultGridColumn(COLUMN_ID_NAME,
						textProvider.getText(titleKey != null ? titleKey
								: Texts.fileListColumnTitleName.name()),
						col.hasValue("sortable") ? col.getBoolean("sortable")
								: true);
			else if (COLUMN_ID_TYPE.equals(id))
				column = new DefaultGridColumn(COLUMN_ID_TYPE,
						textProvider.getText(titleKey != null ? titleKey
								: Texts.fileListColumnTitleType.name()),
						col.hasValue("sortable") ? col.getBoolean("sortable")
								: true);
			else if (COLUMN_ID_SIZE.equals(id))
				column = new DefaultGridColumn(COLUMN_ID_SIZE,
						textProvider.getText(titleKey != null ? titleKey
								: Texts.fileListColumnTitleSize.name()),
						col.hasValue("sortable") ? col.getBoolean("sortable")
								: true);
			else {
				column = pluginEnv.getFileListExt().getColumn(
						id,
						titleKey,
						col.hasValue("sortable") ? col.getBoolean("sortable")
								: true);
			}
			if (column != null)
				c.add(column);
		}
		if (c.isEmpty())
			throw new RuntimeException("Column setup empty");

		return c;
	};

	@Override
	public GridData getData(FileSystemItem item, GridColumn column) {
		if (isCoreColumn(column.getId()))
			return super.getData(item, column);
		return pluginEnv.getFileListExt().getData(column, item, data);
	};

	@Override
	public void setContent(List<FileSystemItem> items, JsObj data) {
		this.data = data;
		setContent(items);
	}

	@Override
	public void sortColumn(String columnId, SortOrder sort) {
		setComparator(isCoreColumn(columnId) ? new DefaultFileItemComparator(
				columnId, sort) : pluginEnv.getFileListExt().getComparator(
				columnId, sort, data));
	}

	private boolean isCoreColumn(String columnId) {
		return FileList.COLUMN_ID_NAME.equals(columnId)
				|| FileList.COLUMN_ID_TYPE.equals(columnId)
				|| FileList.COLUMN_ID_SIZE.equals(columnId);
	}

	@Override
	protected void onRenderFinished() {
		for (GridColumn col : getColumns())
			if (!isCoreColumn(col.getId()))
				pluginEnv.getFileListExt().onFileListRendered(col);
		super.onRenderFinished();
	}

	@Override
	public Widget getWidget() {
		return this;
	}
}
