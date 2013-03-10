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

import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.filesystem.js.JsFile;
import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsFolderHierarchyInfo;
import org.sjarvela.mollify.client.filesystem.js.JsFolderInfo;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.UserCache;

import com.google.gwt.core.client.JavaScriptObject;

public interface FileSystemService {

	void getFolders(JsFolder parent, ResultListener<List<JsFolder>> listener);

	void getFolderInfo(JsFolder parent, JavaScriptObject data,
			ResultListener<JsFolderInfo> listener);

	void getFolderInfoWithHierarchy(String id, JavaScriptObject data,
			ResultListener<JsFolderHierarchyInfo> listener);

	void rename(JsFilesystemItem item, String newName,
			ResultListener<Boolean> listener);

	void copy(JsFilesystemItem item, JsFolder directory,
			ResultListener<Boolean> listener);

	void copy(List<JsFilesystemItem> items, JsFolder directory,
			ResultListener<Boolean> listener);

	void copyWithName(JsFile file, String name, ResultListener listener);

	void move(JsFilesystemItem file, JsFolder toDirectory,
			ResultListener<Boolean> listener);

	void move(List<JsFilesystemItem> items, JsFolder directory,
			ResultListener<Boolean> listener);

	void delete(JsFilesystemItem item, ResultListener<Boolean> listener);

	void delete(List<JsFilesystemItem> items, ResultListener<Boolean> listener);

	void createFolder(JsFolder parentFolder, String folderName,
			ResultListener<Boolean> resultListener);

	String getDownloadUrl(JsFile file);

	String getDownloadUrl(JsFile file, String sessionId);

	String getDownloadAsZipUrl(JsFilesystemItem item);

	void getDownloadAsZipUrl(List<JsFilesystemItem> items,
			ResultListener<String> listener);

	void setItemDescription(JsFilesystemItem item, String description,
			ResultListener listener);

	void removeItemDescription(JsFilesystemItem item, ResultListener listener);

	void getItemDetails(JsFilesystemItem item, JavaScriptObject data,
			ResultListener<ItemDetails> listener);

	void getItemPermissions(JsFilesystemItem item,
			ResultListener<List<FileItemUserPermission>> resultListener,
			UserCache userCache, FileSystemItemCache fileSystemItemCache);

	void updateItemPermissions(List<FileItemUserPermission> newPermissions,
			List<FileItemUserPermission> modifiedPermissions,
			List<FileItemUserPermission> removedPermissions,
			ResultListener resultListener);

	String getPublicLink(JsFile file);

	void retrieveUrl(JsFolder folder, String url, ResultListener listener);

	void search(JsFolder parent, String text,
			ResultListener<SearchResult> listener);

	String getThumbnailUrl(JsFilesystemItem item);

}