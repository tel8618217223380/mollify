/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderContent;
import org.sjarvela.mollify.client.filesystem.FolderDetails;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListenerFactory;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.UserCache;

public class FileSystemServiceAdapter implements FileSystemService {
	private final FileSystemService service;
	private final ResultListenerFactory resultListenerFactory;

	public FileSystemServiceAdapter(FileSystemService service,
			ResultListenerFactory resultListenerFactory) {
		this.service = service;
		this.resultListenerFactory = resultListenerFactory;
	}

	public void copy(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener) {
		service.copy(item, directory, resultListenerFactory
				.createListener(listener));
	}

	public void copy(List<FileSystemItem> items, Folder folder,
			ResultListener<Boolean> listener) {
		service.copy(items, folder, resultListenerFactory
				.createListener(listener));
	}

	public void createFolder(Folder parentFolder, String folderName,
			ResultListener<Boolean> resultListener) {
		service.createFolder(parentFolder, folderName, resultListenerFactory
				.createListener(resultListener));
	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		service.delete(item, resultListenerFactory.createListener(listener));
	}

	@Override
	public void delete(List<FileSystemItem> items,
			ResultListener<Boolean> listener) {
		service.delete(items, listener);
	}

	public void getFolders(Folder parent, ResultListener<List<Folder>> listener) {
		service.getFolders(parent, resultListenerFactory
				.createListener(listener));
	}

	public void getItems(Folder parent, ResultListener<FolderContent> listener) {
		service
				.getItems(parent, resultListenerFactory
						.createListener(listener));
	}

	public void getFolderDetails(Folder directory,
			ResultListener<FolderDetails> resultListener) {
		service.getFolderDetails(directory, resultListenerFactory
				.createListener(resultListener));
	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		return service.getDownloadAsZipUrl(item);
	}

	public String getDownloadUrl(File file) {
		return service.getDownloadUrl(file);
	}
	
	@Override
	public String getDownloadUrl(File file, String sessionId) {
		return service.getDownloadUrl(file, sessionId);
	}

	@Override
	public void getDownloadAsZipUrl(List<FileSystemItem> items,
			ResultListener<String> listener) {
		service.getDownloadAsZipUrl(items, listener);
	}

	public void getFileDetails(File file, ResultListener<FileDetails> listener) {
		service.getFileDetails(file, resultListenerFactory
				.createListener(listener));
	}

	public void getItemPermissions(FileSystemItem item,
			ResultListener<List<FileItemUserPermission>> resultListener,
			UserCache userCache, FileSystemItemCache fileSystemItemCache) {
		service
				.getItemPermissions(item, resultListenerFactory
						.createListener(resultListener), userCache,
						fileSystemItemCache);
	}

	public void move(FileSystemItem file, Folder toDirectory,
			ResultListener<Boolean> listener) {
		service.move(file, toDirectory, resultListenerFactory
				.createListener(listener));
	}

	@Override
	public void move(List<FileSystemItem> items, Folder folder,
			ResultListener<Boolean> listener) {
		service.move(items, folder, resultListenerFactory
				.createListener(listener));
	}

	public void removeItemDescription(FileSystemItem item,
			ResultListener listener) {
		service.removeItemDescription(item, resultListenerFactory
				.createListener(listener));
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		service.rename(item, newName, resultListenerFactory
				.createListener(listener));
	}

	public void setItemDescription(FileSystemItem item, String description,
			ResultListener listener) {
		service.setItemDescription(item, description, listener);
	}

	public void updateItemPermissions(
			List<FileItemUserPermission> newPermissions,
			List<FileItemUserPermission> modifiedPermissions,
			List<FileItemUserPermission> removedPermissions,
			ResultListener resultListener) {
		service.updateItemPermissions(newPermissions, modifiedPermissions,
				removedPermissions, resultListenerFactory
						.createListener(resultListener));
	}

}
