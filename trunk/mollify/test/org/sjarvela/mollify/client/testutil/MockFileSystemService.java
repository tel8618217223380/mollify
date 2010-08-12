/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.testutil;

import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderDetails;
import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.UserCache;

public class MockFileSystemService implements FileSystemService {

	private List<FileItemUserPermission> permissions = Collections.EMPTY_LIST;
	private List<FileItemUserPermission> newPermissions;
	private List<FileItemUserPermission> modifiedPermissions;
	private List<FileItemUserPermission> removedPermissions;

	public void copy(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copy(List<FileSystemItem> items, Folder directory,
			ResultListener<Boolean> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyWithName(File file, String name, ResultListener listener) {
		// TODO Auto-generated method stub

	}

	public void createFolder(Folder parentFolder, String folderName,
			ResultListener<Boolean> resultListener) {
		// TODO Auto-generated method stub

	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(List<FileSystemItem> items,
			ResultListener<Boolean> listener) {
		// TODO Auto-generated method stub

	}

	public void getFolders(Folder parent, ResultListener<List<Folder>> listener) {
		// TODO Auto-generated method stub

	}

	public void getInfo(Folder parent, ResultListener<FolderInfo> listener) {
		// TODO Auto-generated method stub

	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getDownloadAsZipUrl(List<FileSystemItem> items,
			ResultListener<String> listener) {
		// TODO Auto-generated method stub
	}

	public String getDownloadUrl(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDownloadUrl(File file, String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void getItemPermissions(FileSystemItem item,
			ResultListener<List<FileItemUserPermission>> resultListener,
			UserCache userCache, FileSystemItemCache fileSystemItemCache) {
		resultListener.onSuccess(permissions);
	}

	public void move(FileSystemItem file, Folder toDirectory,
			ResultListener<Boolean> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(List<FileSystemItem> items, Folder directory,
			ResultListener<Boolean> listener) {
		// TODO Auto-generated method stub

	}

	public void removeItemDescription(FileSystemItem item,
			ResultListener listener) {
		// TODO Auto-generated method stub

	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		// TODO Auto-generated method stub

	}

	public void setItemDescription(FileSystemItem item, String description,
			ResultListener listener) {
		// TODO Auto-generated method stub

	}

	public void updateItemPermissions(
			List<FileItemUserPermission> newPermissions,
			List<FileItemUserPermission> modifiedPermissions,
			List<FileItemUserPermission> removedPermissions,
			ResultListener resultListener) {
		this.newPermissions = newPermissions;
		this.modifiedPermissions = modifiedPermissions;
		this.removedPermissions = removedPermissions;
		resultListener.onSuccess(true);
	}

	public void getFileDetails(File file, ResultListener<FileDetails> listener) {
		// TODO Auto-generated method stub

	}

	public void getFolderDetails(Folder directory,
			ResultListener<FolderDetails> resultListener) {
		// TODO Auto-generated method stub

	}

	public void setPermissions(List<FileItemUserPermission> permissions) {
		this.permissions = permissions;
	}

	public List<FileItemUserPermission> getNewPermissions() {
		return newPermissions;
	}

	public List<FileItemUserPermission> getModifiedPermissions() {
		return modifiedPermissions;
	}

	public List<FileItemUserPermission> getRemovedPermissions() {
		return removedPermissions;
	}

	@Override
	public String getPublicLink(File file) {
		// TODO Auto-generated method stub
		return null;
	}
}
