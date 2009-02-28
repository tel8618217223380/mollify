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

	public int compare(FileSystemItem f1, FileSystemItem f2) {
		if (f1.isFile() && !f2.isFile())
			return 1;
		if (f2.isFile() && !f1.isFile())
			return -1;

		String i1 = getData(f1);
		String i2 = getData(f2);

		return i1.compareTo(i2) * sort.getCompareFactor();
	}

	private String getData(FileSystemItem item) {
		if (column.equals(FileList.COLUMN_NAME))
			return item.getName();
		if (column.equals(FileList.COLUMN_SIZE) && item.isFile())
			return String.valueOf(((File) item).getSize());

		if (column.equals(FileList.COLUMN_TYPE) && item.isFile())
			return String.valueOf(((File) item).getExtension());

		return "";
	}

}
