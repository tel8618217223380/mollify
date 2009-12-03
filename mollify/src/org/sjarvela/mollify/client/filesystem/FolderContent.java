package org.sjarvela.mollify.client.filesystem;

import java.util.ArrayList;
import java.util.List;

public class FolderContent {
	final List<Folder> dirs;
	final List<File> files;

	public FolderContent() {
		this(new ArrayList(), new ArrayList());
	}
	
	public FolderContent(List<Folder> dirs, List<File> files) {
		this.dirs = dirs;
		this.files = files;
	}

	public List<Folder> getDirectories() {
		return dirs;
	}

	public List<File> getFiles() {
		return files;
	}
}
