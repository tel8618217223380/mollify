package org.sjarvela.mollify.client.filesystem;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.session.file.FilePermission;

public class FolderInfo {
	private final List<Folder> folders;
	private final List<File> files;
	private final List<String> sharedFrom;
	private final List<String> sharedTo;
	private final FilePermission permission;
	private final int quota;
	private final int quotaUsed;

	public FolderInfo() {
		this(FilePermission.None, new ArrayList(), new ArrayList(), 0, 0,
				new ArrayList(), new ArrayList());
	}

	public FolderInfo(FilePermission permission, List<Folder> folders,
			List<File> files, int quota, int quotaUsed,
			List<String> sharedFrom, List<String> sharedTo) {
		this.permission = permission;
		this.folders = folders;
		this.files = files;
		this.quota = quota;
		this.quotaUsed = quotaUsed;
		this.sharedFrom = sharedFrom;
		this.sharedTo = sharedTo;
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

	public int getQuota() {
		return quota;
	}

	public int getQuotaUsed() {
		return quotaUsed;
	}

	public List<String> getSharedFrom() {
		return sharedFrom;
	}

	public List<String> getSharedTo() {
		return sharedTo;
	}

}
