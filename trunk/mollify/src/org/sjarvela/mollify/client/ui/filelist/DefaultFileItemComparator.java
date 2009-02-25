package org.sjarvela.mollify.client.ui.filelist;

import java.util.Comparator;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;

public class DefaultFileItemComparator implements Comparator<FileSystemItem> {

	public int compare(FileSystemItem f1, FileSystemItem f2) {
		if (f1.isFile() && !f2.isFile())
			return 1;
		if (f2.isFile() && !f1.isFile())
			return -1;

		return f1.getName().compareTo(f2.getName());
	}

}
