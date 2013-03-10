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
import org.sjarvela.mollify.client.service.request.listener.ResultListenerFactory;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.UserCache;

import com.google.gwt.core.client.JavaScriptObject;

public class FileSystemServiceAdapter implements FileSystemService {
	private final FileSystemService service;
	private final ResultListenerFactory resultListenerFactory;

	public FileSystemServiceAdapter(FileSystemService service,
			ResultListenerFactory resultListenerFactory) {
		this.service = service;
		this.resultListenerFactory = resultListenerFactory;
	}

	public void copy(JsFilesystemItem item, JsFolder directory,
			ResultListener<Boolean> listener) {
		service.copy(item, directory,
				resultListenerFactory.createListener(listener));
	}

	public void copy(List<JsFilesystemItem> items, JsFolder folder,
			ResultListener<Boolean> listener) {
		service.copy(items, folder,
				resultListenerFactory.createListener(listener));
	}

	@Override
	public void copyWithName(JsFile file, String name, ResultListener listener) {
		service.copyWithName(file, name,
				resultListenerFactory.createListener(listener));
	}

	public void createFolder(JsFolder parentFolder, String folderName,
			ResultListener<Boolean> resultListener) {
		service.createFolder(parentFolder, folderName,
				resultListenerFactory.createListener(resultListener));
	}

	public void delete(JsFilesystemItem item, ResultListener<Boolean> listener) {
		service.delete(item, resultListenerFactory.createListener(listener));
	}

	@Override
	public void delete(List<JsFilesystemItem> items,
			ResultListener<Boolean> listener) {
		service.delete(items, listener);
	}

	public void getFolders(JsFolder parent,
			ResultListener<List<JsFolder>> listener) {
		service.getFolders(parent,
				resultListenerFactory.createListener(listener));
	}

	public void getFolderInfo(JsFolder parent, JavaScriptObject data,
			ResultListener<JsFolderInfo> listener) {
		service.getFolderInfo(parent, data,
				resultListenerFactory.createListener(listener));
	}

	@Override
	public void getFolderInfoWithHierarchy(String id, JavaScriptObject data,
			ResultListener<JsFolderHierarchyInfo> listener) {
		service.getFolderInfoWithHierarchy(id, data,
				resultListenerFactory.createListener(listener));
	}

	public String getDownloadAsZipUrl(JsFilesystemItem item) {
		return service.getDownloadAsZipUrl(item);
	}

	public String getDownloadUrl(JsFile file) {
		return service.getDownloadUrl(file);
	}

	@Override
	public String getDownloadUrl(JsFile file, String sessionId) {
		return service.getDownloadUrl(file, sessionId);
	}

	@Override
	public void getDownloadAsZipUrl(List<JsFilesystemItem> items,
			ResultListener<String> listener) {
		service.getDownloadAsZipUrl(items, listener);
	}

	@Override
	public void getItemDetails(JsFilesystemItem item, JavaScriptObject data,
			ResultListener<ItemDetails> listener) {
		service.getItemDetails(item, data,
				resultListenerFactory.createListener(listener));
	}

	public void getItemPermissions(JsFilesystemItem item,
			ResultListener<List<FileItemUserPermission>> resultListener,
			UserCache userCache, FileSystemItemCache fileSystemItemCache) {
		service.getItemPermissions(item,
				resultListenerFactory.createListener(resultListener),
				userCache, fileSystemItemCache);
	}

	public void move(JsFilesystemItem file, JsFolder toDirectory,
			ResultListener<Boolean> listener) {
		service.move(file, toDirectory,
				resultListenerFactory.createListener(listener));
	}

	@Override
	public void move(List<JsFilesystemItem> items, JsFolder folder,
			ResultListener<Boolean> listener) {
		service.move(items, folder,
				resultListenerFactory.createListener(listener));
	}

	public void removeItemDescription(JsFilesystemItem item,
			ResultListener listener) {
		service.removeItemDescription(item,
				resultListenerFactory.createListener(listener));
	}

	public void rename(JsFilesystemItem item, String newName,
			ResultListener<Boolean> listener) {
		service.rename(item, newName,
				resultListenerFactory.createListener(listener));
	}

	public void setItemDescription(JsFilesystemItem item, String description,
			ResultListener listener) {
		service.setItemDescription(item, description, listener);
	}

	public void updateItemPermissions(
			List<FileItemUserPermission> newPermissions,
			List<FileItemUserPermission> modifiedPermissions,
			List<FileItemUserPermission> removedPermissions,
			ResultListener resultListener) {
		service.updateItemPermissions(newPermissions, modifiedPermissions,
				removedPermissions,
				resultListenerFactory.createListener(resultListener));
	}

	@Override
	public String getPublicLink(JsFile file) {
		return service.getPublicLink(file);
	}

	@Override
	public void retrieveUrl(JsFolder folder, String url, ResultListener listener) {
		service.retrieveUrl(folder, url,
				resultListenerFactory.createListener(listener));
	}

	@Override
	public void search(JsFolder parent, String text,
			ResultListener<SearchResult> listener) {
		service.search(parent, text,
				resultListenerFactory.createListener(listener));
	}

	@Override
	public String getThumbnailUrl(JsFilesystemItem item) {
		return service.getThumbnailUrl(item);
	}

}
