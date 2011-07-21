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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.filelist.DefaultFileItemComparator;
import org.sjarvela.mollify.client.ui.filelist.FileList;

public class SearchResultsComparator extends DefaultFileItemComparator {
	public SearchResultsComparator(String columnId, SortOrder sort) {
		super(columnId, sort);
	}

	@Override
	public int compare(FileSystemItem item1, FileSystemItem item2) {
		if (getColumnId().equals(FileList.COLUMN_ID_NAME))
			return item1.getName().compareTo(item2.getName())
					* sort.getCompareFactor();

		if (getColumnId().equals(FileList.COLUMN_ID_SIZE))
			return compareSize(item1, item2);

		if (getColumnId().equals(SearchResultFileList.COLUMN_ID_PATH))
			return item1.getPath().compareTo(item2.getPath())
					* sort.getCompareFactor();

		return 0;
	}

	private int compareSize(FileSystemItem item1, FileSystemItem item2) {
		if (item1.isFile() && !item2.isFile())
			return 1;
		if (item2.isFile() && !item1.isFile())
			return -1;

		return item1.isFile() ? getSizeCompare(item1, item2) : 0;
	}

}
