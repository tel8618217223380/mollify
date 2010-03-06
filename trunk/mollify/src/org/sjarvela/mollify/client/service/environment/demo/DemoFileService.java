/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.demo;

import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderContent;
import org.sjarvela.mollify.client.filesystem.FolderDetails;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.UserCache;

public class DemoFileService implements FileSystemService {
	private final DemoData data;

	public DemoFileService(DemoData data) {
		this.data = data;
	}

	public void createFolder(Folder parentFolder, String folderName,
			ResultListener<Boolean> resultListener) {
		resultListener.onSuccess(true);
	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	@Override
	public void delete(List<FileSystemItem> item,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void getFolders(Folder parent, ResultListener<List<Folder>> listener) {
		listener.onSuccess(data.getDirectories(parent));
	}

	public void getItems(Folder parent, ResultListener<FolderContent> listener) {
		listener.onSuccess(new FolderContent(data.getDirectories(parent), data
				.getFiles(parent)));
	}

	public String getDownloadUrl(File file) {
		return DemoEnvironment.MOLLIFY_PACKAGE_URL;
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void copy(File file, Folder directory,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	@Override
	public void copy(List<FileSystemItem> items, Folder directory,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void move(FileSystemItem item, Folder folder,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	@Override
	public void move(List<FileSystemItem> items, Folder folder,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void getFileDetails(File file, ResultListener<FileDetails> listener) {
		listener.onSuccess(data.getFileDetails(file));
	}

	public void getFolderDetails(Folder directory,
			ResultListener<FolderDetails> resultListener) {
		resultListener.onSuccess(data.getDirectoryDetails(directory));
	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		return DemoEnvironment.MOLLIFY_PACKAGE_URL;
	}

	public void removeItemDescription(FileSystemItem item,
			ResultListener listener) {
		listener.onSuccess(true);
	}

	public void setItemDescription(FileSystemItem item, String description,
			ResultListener listener) {
		listener.onSuccess(true);
	}

	public void getItemPermissions(FileSystemItem item,
			ResultListener<List<FileItemUserPermission>> resultListener,
			UserCache userCache, FileSystemItemCache itemCache) {
		FileItemUserPermission defaultPermission = new FileItemUserPermission(
				item, null, FilePermission.ReadOnly);
		FileItemUserPermission p1 = new FileItemUserPermission(item, data
				.getUsers().get(0), FilePermission.ReadOnly);
		FileItemUserPermission p2 = new FileItemUserPermission(item, data
				.getUsers().get(1), FilePermission.ReadWrite);
		resultListener.onSuccess(Arrays.asList(defaultPermission, p1, p2));
	}

	public void updateItemPermissions(
			List<FileItemUserPermission> newPermissions,
			List<FileItemUserPermission> modifiedPermissions,
			List<FileItemUserPermission> removedPermissions,
			ResultListener listener) {
		listener.onSuccess(true);
	}
}
