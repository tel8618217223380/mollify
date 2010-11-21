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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.filesystem.js.JsFolderInfo;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.JSONStringBuilder;
import org.sjarvela.mollify.client.service.request.JSONStringBuilder.JSONArrayBuilder;
import org.sjarvela.mollify.client.service.request.UrlBuilder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.file.js.JsFileItemUserPermission;
import org.sjarvela.mollify.client.session.user.UserCache;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.logging.client.LogConfiguration;

public class PhpFileService extends ServiceBase implements FileSystemService {
	private static Logger logger = Logger.getLogger(PhpFileService.class
			.getName());

	enum FileAction implements ActionId {
		files, folders, info, details, name, copy, move, delete, download, upload, zip, description, permissions, retrieveUrl
	};

	public PhpFileService(PhpService service) {
		super(service, RequestType.filesystem);
	}

	public void getFolders(Folder parent,
			final ResultListener<List<Folder>> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Get directories: " + parent.getId());

		ResultListener<JsArray> resultListener = new ResultListener<JsArray>() {
			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(JsArray result) {
				listener.onSuccess(FileSystemItem.createFromFolders(result));
			}
		};
		request().url(serviceUrl().fileItem(parent).action(FileAction.folders))
				.listener(resultListener).get();
	}

	public void getInfo(final Folder parent,
			final ResultListener<FolderInfo> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Get folder items: " + parent.getId());

		ResultListener<JsFolderInfo> resultListener = new ResultListener<JsFolderInfo>() {
			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(JsFolderInfo result) {
				listener.onSuccess(new FolderInfo(result.getPermission(),
						FileSystemItem.createFromFolders(result.getFolders()),
						FileSystemItem.createFromFiles(result.getFiles())));
			}
		};

		request().url(serviceUrl().fileItem(parent).action(FileAction.info))
				.listener(resultListener).get();
	}

	public void getItemDetails(FileSystemItem item,
			ResultListener<ItemDetails> resultListener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Get details: " + item.getId());

		request().url(serviceUrl().fileItem(item).action(FileAction.details))
				.listener(resultListener).get();
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Rename " + item.getId() + " to [" + newName
					+ "]");
		String data = new JSONStringBuilder("name", newName).toString();

		request().url(serviceUrl().fileItem(item).action(FileAction.name))
				.data(data).listener(listener).put();
	}

	public void copy(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO,
					"Copy " + item.getId() + " to [" + directory.getId() + "]");
		String data = new JSONStringBuilder("folder", directory.getId())
				.toString();

		request().url(serviceUrl().fileItem(item).action(FileAction.copy))
				.data(data).listener(listener).post();
	}

	@Override
	public void copyWithName(File file, String name, ResultListener listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Copy " + file.getId() + " with name ["
					+ name + "]");
		String data = new JSONStringBuilder("name", name).toString();

		request().url(serviceUrl().fileItem(file).action(FileAction.copy))
				.data(data).listener(listener).post();
	}

	public void copy(List<FileSystemItem> items, Folder folder,
			ResultListener<Boolean> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Copy " + items.size() + " items to ["
					+ folder.getId() + "]");
		JSONStringBuilder data = new JSONStringBuilder("action", "copy").add(
				"to", folder.getId());
		JSONArrayBuilder itemArray = data.addArray("items");
		for (FileSystemItem item : items)
			itemArray.add(item.getId());

		request().url(serviceUrl().item("items")).listener(listener)
				.data(data.toString()).post();
	}

	public void move(FileSystemItem item, Folder directory,
			ResultListener<Boolean> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO,
					"Move " + item.getId() + " to [" + directory.getId() + "]");
		String data = new JSONStringBuilder("id", directory.getId()).toString();

		request().url(serviceUrl().fileItem(item).action(FileAction.move))
				.data(data).listener(listener).post();
	}

	public void move(List<FileSystemItem> items, Folder folder,
			ResultListener<Boolean> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Move " + items.size() + " items to ["
					+ folder.getId() + "]");
		JSONStringBuilder data = new JSONStringBuilder("action", "move").add(
				"to", folder.getId());
		JSONArrayBuilder itemArray = data.addArray("items");
		for (FileSystemItem item : items)
			itemArray.add(item.getId());

		request().url(serviceUrl().item("items")).listener(listener)
				.data(data.toString()).post();
	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Delete: " + item.getId());

		request().url(serviceUrl().fileItem(item)).listener(listener).delete();
	}

	@Override
	public void delete(List<FileSystemItem> items,
			ResultListener<Boolean> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Delete " + items.size() + " items");

		JSONStringBuilder data = new JSONStringBuilder("action", "delete");
		JSONArrayBuilder itemArray = data.addArray("items");
		for (FileSystemItem item : items)
			itemArray.add(item.getId());

		request().url(serviceUrl().item("items")).listener(listener)
				.data(data.toString()).post();
	}

	public void createFolder(Folder parentFolder, String folderName,
			ResultListener<Boolean> listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Create directory: [" + folderName + "]");
		String data = new JSONStringBuilder("name", folderName).toString();

		request()
				.url(serviceUrl().fileItem(parentFolder).action(
						FileAction.folders)).data(data).listener(listener)
				.post();
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
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Download as zip " + items.size() + " items");

		JSONStringBuilder data = new JSONStringBuilder("action", "zip");
		JSONArrayBuilder itemArray = data.addArray("items");
		for (FileSystemItem item : items)
			itemArray.add(item.getId());

		request().url(serviceUrl().item("items"))
				.listener(new ResultListener<Boolean>() {
					@Override
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					@Override
					public void onSuccess(Boolean result) {
						listener.onSuccess(serviceUrl().item("items")
								.action(FileAction.zip).build());
					}
				}).data(data.toString()).post();
	}

	@Override
	public void setItemDescription(FileSystemItem item, String description,
			ResultListener listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Set description: " + item.getId());

		request()
				.url(serviceUrl().fileItem(item).action(FileAction.description))
				.data(new JSONStringBuilder("description", description)
						.toString()).listener(listener).put();
	}

	@Override
	public void removeItemDescription(FileSystemItem item,
			ResultListener listener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Remove description: " + item.getId());

		request()
				.url(serviceUrl().fileItem(item).action(FileAction.description))
				.listener(listener).delete();
	}

	@Override
	public void getItemPermissions(FileSystemItem item,
			final ResultListener<List<FileItemUserPermission>> resultListener,
			final UserCache userCache, final FileSystemItemCache itemCache) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Get user permissions: " + item.getId());

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

		request()
				.url(serviceUrl().fileItem(item).action(FileAction.permissions))
				.listener(listener).get();
	}

	@Override
	public void updateItemPermissions(
			List<FileItemUserPermission> newPermissions,
			List<FileItemUserPermission> modifiedPermissions,
			List<FileItemUserPermission> removedPermissions,
			final ResultListener resultListener) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "Update item permissions");

		JSONStringBuilder data = new JSONStringBuilder();
		data.add("new", FileItemUserPermission.asJsArray(newPermissions));
		data.add("modified",
				FileItemUserPermission.asJsArray(modifiedPermissions));
		data.add("removed",
				FileItemUserPermission.asJsArray(removedPermissions));

		request().url(serviceUrl().action(FileAction.permissions))
				.data(data.toString()).listener(resultListener).put();
	}

	@Override
	public String getPublicLink(File file) {
		return service.serviceUrl().item("public").item("items").fileItem(file)
				.build();
	}

	@Override
	public void retrieveUrl(Folder folder, String url, ResultListener listener) {
		request().url(serviceUrl().fileItem(folder).item("retrieve"))
				.data(new JSONStringBuilder("url", url).toString())
				.listener(listener).post();
	}

	@Override
	public void search(Folder parent, String text,
			ResultListener<SearchResult> listener) {
		UrlBuilder url = serviceUrl();
		if (parent != null)
			url.fileItem(parent);

		request().url(url.item("search"))
				.data(new JSONStringBuilder("text", text).toString())
				.listener(listener).post();
	}
}
