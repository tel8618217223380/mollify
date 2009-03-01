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

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.Sort;

public class DefaultFileItemComparator implements
		GridComparator<FileSystemItem> {
	private final GridColumn column;
	private final Sort sort;

	public DefaultFileItemComparator(GridColumn column, Sort sort) {
		this.column = column;
		this.sort = sort;
	}

	public GridColumn getColumn() {
		return column;
	}

	public Sort getSort() {
		return sort;
	}

	public int compare(FileSystemItem item1, FileSystemItem item2) {
		if (item1.isFile() && !item2.isFile())
			return 1;
		if (item2.isFile() && !item1.isFile())
			return -1;

		if (FileList.COLUMN_SIZE.equals(column))
			return (getSize(item1) - getSize(item2)) * sort.getCompareFactor();

		return getData(item1).compareToIgnoreCase(getData(item2))
				* sort.getCompareFactor();
	}

	private int getSize(FileSystemItem item) {
		if (item.isFile())
			return ((File) item).getSize();
		return 0;
	}

	private String getData(FileSystemItem item) {
		if (column.equals(FileList.COLUMN_NAME))
			return item.getName();

		if (column.equals(FileList.COLUMN_TYPE) && item.isFile())
			return String.valueOf(((File) item).getExtension());

		return "";
	}

}
