package org.sjarvela.mollify.client.filesystem;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.session.file.FilePermission;

public class FolderInfo {
	private final List<Folder> folders;
	private final List<File> files;
	private final FilePermission permission;

	public FolderInfo() {
		this(FilePermission.None, new ArrayList(), new ArrayList());
	}

	public FolderInfo(FilePermission permission, List<Folder> folders,
			List<File> files) {
		this.permission = permission;
		this.folders = folders;
		this.files = files;
	}

	public FilePermission getPermission() {
		return permission;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public List<File> getFiles() {
		return files;
	}
}
