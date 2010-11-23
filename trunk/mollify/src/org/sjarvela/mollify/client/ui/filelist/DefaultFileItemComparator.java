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
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.Sort;

public class DefaultFileItemComparator implements
		GridComparator<FileSystemItem> {
	private final String columnId;
	private final Sort sort;

	public DefaultFileItemComparator(String columnId, Sort sort) {
		this.columnId = columnId;
		this.sort = sort;
	}

	public String getColumnId() {
		return columnId;
	}

	public Sort getSort() {
		return sort;
	}

	public int compare(FileSystemItem item1, FileSystemItem item2) {
		if (Folder.Parent.equals(item1) && !Folder.Parent.equals(item2))
			return -1;
		if (Folder.Parent.equals(item2) && !Folder.Parent.equals(item1))
			return 1;
		if (item1.isFile() && !item2.isFile())
			return 1;
		if (item2.isFile() && !item1.isFile())
			return -1;

		if (FileList.COLUMN_ID_SIZE.equals(columnId))
			return item1.isFile() ? getSizeCompare(item1, item2) : 0;

		return getData(item1).compareToIgnoreCase(getData(item2))
				* sort.getCompareFactor();
	}

	private int getSizeCompare(FileSystemItem item1, FileSystemItem item2) {
		int diff = Math.abs((int) ((File) item1).getSize())
				- Math.abs((int) ((File) item2).getSize());
		return diff * sort.getCompareFactor();
	}

	private String getData(FileSystemItem item) {
		if (FileList.COLUMN_ID_NAME.equals(columnId))
			return item.getName();

		if (FileList.COLUMN_ID_TYPE.equals(columnId) && item.isFile())
			return String.valueOf(((File) item).getExtension());

		return "";
	}

}
