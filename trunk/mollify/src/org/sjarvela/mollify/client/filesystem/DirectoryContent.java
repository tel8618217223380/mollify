package org.sjarvela.mollify.client.filesystem;

import java.util.List;

public class DirectoryContent {
	final List<Directory> dirs;
	final List<File> files;

	public DirectoryContent(List<Directory> dirs, List<File> files) {
		this.dirs = dirs;
		this.files = files;
	}

	public List<Directory> getDirectories() {
		return dirs;
	}

	public List<File> getFiles() {
		return files;
	}
}
