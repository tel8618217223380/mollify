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
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderHierarchyInfo;
import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.request.JSONBuilder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.UserCache;

import com.google.gwt.core.client.JavaScriptObject;

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
		listener.onSuccess(data.getFolders(parent));
	}

	public void getFolderInfo(Folder parent, JavaScriptObject d,
			ResultListener<FolderInfo> listener) {
		listener.onSuccess(new FolderInfo(FilePermission.ReadWrite, data
				.getFolders(parent), data.getFiles(parent), null));
	}

	@Override
	public void getFolderInfoWithHierarchy(String id,
			ResultListener<FolderHierarchyInfo> listener) {
		Folder parent = data.folders.get(DemoData.ROOT_1).get(0);
		Folder root = new Folder(DemoData.ROOT_1, "", "Folder A", "", "");
		listener.onSuccess(new FolderHierarchyInfo(FilePermission.ReadWrite,
				data.getFolders(parent), data.getFiles(parent), Arrays
						.asList(root)));
	}

	public String getDownloadUrl(File file) {
		return DemoEnvironment.MOLLIFY_PACKAGE_URL;
	}

	@Override
	public String getDownloadUrl(File file, String sessionId) {
		return DemoEnvironment.MOLLIFY_PACKAGE_URL;
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void copy(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	@Override
	public void copy(List<FileSystemItem> items, Folder directory,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	@Override
	public void copyWithName(File file, String name, ResultListener listener) {
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

	public void getItemDetails(FileSystemItem item, JavaScriptObject d,
			ResultListener<ItemDetails> listener) {
		if (item.isFile())
			listener.onSuccess(data.getFileDetails((File) item));
		else
			listener.onSuccess(data.getFolderDetails((Folder) item));
	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		return DemoEnvironment.MOLLIFY_PACKAGE_URL;
	}

	@Override
	public void getDownloadAsZipUrl(List<FileSystemItem> items,
			ResultListener<String> listener) {
		listener.onSuccess(DemoEnvironment.MOLLIFY_PACKAGE_URL);
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

	@Override
	public String getPublicLink(File file) {
		return "http://www.mollify.org/images/mollify_logo.png";
	}

	@Override
	public void retrieveUrl(Folder folder, String url, ResultListener listener) {
		listener.onSuccess(true);
	}

	@Override
	public void search(Folder parent, String text,
			ResultListener<SearchResult> listener) {
		JSONBuilder result = new JSONBuilder().add("count", 2);
		JSONBuilder matches = result.object("matches");

		JSONBuilder match1 = matches.object("1");
		match1.object("item", data.files.get(0).asJs());
		match1.array("matches").addObject().add("type", "name");

		JSONBuilder match2 = matches.object("2");
		match2.object("item", data.files.get(1).asJs());
		match2.array("matches").addObject().add("type", "name");
		match2.array("matches").addObject().add("type", "description")
				.add("description", "Example description");

		listener.onSuccess((SearchResult) result.toJSON().cast());
	}

	@Override
	public String getThumbnailUrl(FileSystemItem item) {
		return "http://www.mollify.org/images/mollify_logo.png";
	}
}
