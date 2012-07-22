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
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderHierarchyInfo;
import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.filesystem.provider.ItemDetailsProvider;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.UserCache;

import com.google.gwt.core.client.JavaScriptObject;

public interface FileSystemService extends ItemDetailsProvider {

	void getFolders(Folder parent, ResultListener<List<Folder>> listener);

	void getFolderInfo(Folder parent, JavaScriptObject data,
			ResultListener<FolderInfo> listener);

	void getFolderInfoWithHierarchy(String id,
			ResultListener<FolderHierarchyInfo> listener);

	void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener);

	void copy(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener);

	void copy(List<FileSystemItem> items, Folder directory,
			ResultListener<Boolean> listener);

	void copyWithName(File file, String name, ResultListener listener);

	void move(FileSystemItem file, Folder toDirectory,
			ResultListener<Boolean> listener);

	void move(List<FileSystemItem> items, Folder directory,
			ResultListener<Boolean> listener);

	void delete(FileSystemItem item, ResultListener<Boolean> listener);

	void delete(List<FileSystemItem> items, ResultListener<Boolean> listener);

	void createFolder(Folder parentFolder, String folderName,
			ResultListener<Boolean> resultListener);

	String getDownloadUrl(File file);

	String getDownloadUrl(File file, String sessionId);

	String getDownloadAsZipUrl(FileSystemItem item);

	void getDownloadAsZipUrl(List<FileSystemItem> items,
			ResultListener<String> listener);

	void setItemDescription(FileSystemItem item, String description,
			ResultListener listener);

	void removeItemDescription(FileSystemItem item, ResultListener listener);

	void getItemPermissions(FileSystemItem item,
			ResultListener<List<FileItemUserPermission>> resultListener,
			UserCache userCache, FileSystemItemCache fileSystemItemCache);

	void updateItemPermissions(List<FileItemUserPermission> newPermissions,
			List<FileItemUserPermission> modifiedPermissions,
			List<FileItemUserPermission> removedPermissions,
			ResultListener resultListener);

	String getPublicLink(File file);

	void retrieveUrl(Folder folder, String url, ResultListener listener);

	void search(Folder parent, String text,
			ResultListener<SearchResult> listener);

	String getThumbnailUrl(FileSystemItem item);

}