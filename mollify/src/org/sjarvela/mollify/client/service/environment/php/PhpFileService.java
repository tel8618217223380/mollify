/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.php;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderContent;
import org.sjarvela.mollify.client.filesystem.FolderDetails;
import org.sjarvela.mollify.client.filesystem.FoldersAndFiles;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.JSONStringBuilder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.file.js.JsFileItemUserPermission;
import org.sjarvela.mollify.client.session.user.UserCache;
import org.sjarvela.mollify.client.util.JsUtil;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;

public class PhpFileService extends ServiceBase implements FileSystemService {
	enum FileAction implements ActionId {
		files, folders, items, details, name, copy, move, delete, download, upload, zip, description, permissions
	};

	public PhpFileService(PhpService service) {
		super(service, RequestType.filesystem);
	}

	public void getDirectories(Folder parent,
			final ResultListener<List<Folder>> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories: " + parent.getId());

		ResultListener<JsArray> resultListener = new ResultListener<JsArray>() {
			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(JsArray result) {
				listener
						.onSuccess(FileSystemItem.createFromDirectories(result));
			}
		};
		request().url(serviceUrl().fileItem(parent).action(FileAction.folders))
				.listener(resultListener).get();
	}

	public void getDirectoryContents(final Folder parent,
			final ResultListener<FolderContent> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directory contents: " + parent.getId());

		ResultListener<FoldersAndFiles> resultListener = new ResultListener<FoldersAndFiles>() {
			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(FoldersAndFiles result) {
				listener.onSuccess(new FolderContent(FileSystemItem
						.createFromDirectories(result.getFolders()),
						FileSystemItem.createFromFiles(result.getFiles())));
			}
		};

		request().url(serviceUrl().fileItem(parent).action(FileAction.items))
				.listener(resultListener).get();
	}

	public void getFileDetails(File item,
			ResultListener<FileDetails> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get file details: " + item.getId());

		request().url(serviceUrl().fileItem(item).action(FileAction.details))
				.listener(resultListener).get();
	}

	public void getDirectoryDetails(Folder item,
			ResultListener<FolderDetails> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get folder details: " + item.getId());

		request().url(serviceUrl().fileItem(item).action(FileAction.details))
				.listener(resultListener).get();
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Rename " + item.getId() + " to [" + newName + "]");

		request().url(serviceUrl().fileItem(item).action(FileAction.name))
				.data(newName).listener(listener).put();
	}

	public void copy(File file, Folder directory,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Copy " + file.getId() + " to [" + directory.getId()
					+ "]");

		request().url(serviceUrl().fileItem(file).action(FileAction.copy))
				.data(directory.getId()).listener(listener).post();
	}

	public void move(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Move " + item.getId() + " to [" + directory.getId()
					+ "]");

		request().url(serviceUrl().fileItem(item).action(FileAction.move))
				.data(directory.getId()).listener(listener).post();
	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Delete: " + item.getId());

		request().url(serviceUrl().fileItem(item)).listener(listener).delete();
	}

	public void createDirectory(Folder parentFolder, String folderName,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Create directory: [" + folderName + "]");

		request().url(
				serviceUrl().fileItem(parentFolder).action(FileAction.folders))
				.data(folderName).listener(listener).post();
	}

	public String getDownloadUrl(File file) {
		return serviceUrl().fileItem(file).toString();
	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		return serviceUrl().fileItem(item).action(FileAction.zip).toString();
	}

	public void setItemDescription(FileSystemItem item, String description,
			ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Set description: " + item.getId());

		request().url(
				serviceUrl().fileItem(item).action(FileAction.description))
				.data(description).listener(listener).put();
	}

	public void removeItemDescription(FileSystemItem item,
			ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Remove description: " + item.getId());

		request().url(
				serviceUrl().fileItem(item).action(FileAction.description))
				.listener(listener).delete();
	}

	public void getItemPermissions(FileSystemItem item,
			final ResultListener<List<FileItemUserPermission>> resultListener,
			final UserCache userCache, final FileSystemItemCache itemCache) {
		if (Log.isDebugEnabled())
			Log.debug("Get user permissions: " + item.getId());

		ResultListener<JsArray<JsFileItemUserPermission>> listener = new ResultListener<JsArray<JsFileItemUserPermission>>() {
			public void onFail(ServiceError error) {
				resultListener.onFail(error);
			}

			public void onSuccess(JsArray<JsFileItemUserPermission> result) {
				List<JsFileItemUserPermission> permissions = JsUtil.asList(
						result, JsFileItemUserPermission.class);
				resultListener.onSuccess(FileItemUserPermission.convert(
						permissions, userCache, itemCache));
			}
		};

		request().url(
				serviceUrl().fileItem(item).action(FileAction.permissions))
				.listener(listener).get();
	}

	public void updateItemPermissions(
			List<FileItemUserPermission> newPermissions,
			List<FileItemUserPermission> modifiedPermissions,
			List<FileItemUserPermission> removedPermissions,
			final ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Update item permissions");

		JSONStringBuilder data = new JSONStringBuilder();
		data.add("new", FileItemUserPermission.asJsArray(newPermissions));
		data.add("modified", FileItemUserPermission
				.asJsArray(modifiedPermissions));
		data.add("removed", FileItemUserPermission
				.asJsArray(removedPermissions));

		request().url(serviceUrl().action(FileAction.permissions)).data(
				data.toString()).listener(resultListener).put();
	}
}
