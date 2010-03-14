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
import org.sjarvela.mollify.client.service.request.JSONStringBuilder.JSONArrayBuilder;
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

	public void getFolders(Folder parent,
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

	public void getItems(final Folder parent,
			final ResultListener<FolderContent> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get folder items: " + parent.getId());

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

	public void getFolderDetails(Folder item,
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
		String data = new JSONStringBuilder("name", newName).toString();

		request().url(serviceUrl().fileItem(item).action(FileAction.name))
				.data(data).listener(listener).put();
	}

	public void copy(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Copy " + item.getId() + " to [" + directory.getId()
					+ "]");
		String data = new JSONStringBuilder("id", directory.getId()).toString();

		request().url(serviceUrl().fileItem(item).action(FileAction.copy))
				.data(data).listener(listener).post();
	}

	public void copy(List<FileSystemItem> items, Folder folder,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Copy " + items.size() + " items to [" + folder.getId()
					+ "]");
		JSONStringBuilder data = new JSONStringBuilder("action", "copy").add(
				"to", folder.getId());
		JSONArrayBuilder itemArray = data.addArray("items");
		for (FileSystemItem item : items)
			itemArray.add(item.getId());

		request().url(serviceUrl().item("items")).listener(listener).data(
				data.toString()).post();
	}

	public void move(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Move " + item.getId() + " to [" + directory.getId()
					+ "]");
		String data = new JSONStringBuilder("id", directory.getId()).toString();

		request().url(serviceUrl().fileItem(item).action(FileAction.move))
				.data(data).listener(listener).post();
	}

	public void move(List<FileSystemItem> items, Folder folder,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Move " + items.size() + " items to [" + folder.getId()
					+ "]");
		JSONStringBuilder data = new JSONStringBuilder("action", "move").add(
				"to", folder.getId());
		JSONArrayBuilder itemArray = data.addArray("items");
		for (FileSystemItem item : items)
			itemArray.add(item.getId());

		request().url(serviceUrl().item("items")).listener(listener).data(
				data.toString()).post();
	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Delete: " + item.getId());

		request().url(serviceUrl().fileItem(item)).listener(listener).delete();
	}

	@Override
	public void delete(List<FileSystemItem> items,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Delete " + items.size() + " items");

		JSONStringBuilder data = new JSONStringBuilder("action", "delete");
		JSONArrayBuilder itemArray = data.addArray("items");
		for (FileSystemItem item : items)
			itemArray.add(item.getId());

		request().url(serviceUrl().item("items")).listener(listener).data(
				data.toString()).post();
	}

	public void createFolder(Folder parentFolder, String folderName,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Create directory: [" + folderName + "]");
		String data = new JSONStringBuilder("name", folderName).toString();

		request().url(
				serviceUrl().fileItem(parentFolder).action(FileAction.folders))
				.data(data).listener(listener).post();
	}

	public String getDownloadUrl(File file) {
		return serviceUrl().fileItem(file).build();
	}

	@Override
	public String getDownloadUrl(File file, String sessionId) {
		return serviceUrl().fileItem(file).build() + "?session=" + sessionId;
	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		return serviceUrl().fileItem(item).action(FileAction.zip).build();
	}

	@Override
	public void getDownloadAsZipUrl(List<FileSystemItem> items,
			final ResultListener<String> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Download as zip " + items.size() + " items");

		JSONStringBuilder data = new JSONStringBuilder("action", "zip");
		JSONArrayBuilder itemArray = data.addArray("items");
		for (FileSystemItem item : items)
			itemArray.add(item.getId());

		request().url(serviceUrl().item("items")).listener(
				new ResultListener<Boolean>() {
					@Override
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					@Override
					public void onSuccess(Boolean result) {
						listener.onSuccess(serviceUrl().item("items").action(
								FileAction.zip).build());
					}
				}).data(data.toString()).post();
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
