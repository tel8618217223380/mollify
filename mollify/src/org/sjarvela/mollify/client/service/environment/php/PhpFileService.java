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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.DirectoriesAndFiles;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.DirectoryDetails;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.UrlParam;
import org.sjarvela.mollify.client.service.request.data.JSONStringBuilder;
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
		get_files, directories, items, details, rename, copy, move, delete, create_folder, download, upload, download_as_zip, set_description, remove_description, get_item_permissions, update_item_permissions
	};

	public PhpFileService(PhpService service) {
		super(service, RequestType.filesystem);
	}

	public void getDirectories(Directory parent,
			final ResultListener<List<Directory>> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories: " + parent.getId());

		service.doGetRequest(getUrl(parent, FileAction.directories),
				new ResultListener<JsArray>() {
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					public void onSuccess(JsArray result) {
						listener.onSuccess(FileSystemItem
								.createFromDirectories(result));
					}
				});
	}

	public void getDirectoryContents(final Directory parent,
			final ResultListener<DirectoryContent> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directory contents: " + parent.getId());

		service.doGetRequest(getUrl(parent, FileAction.items),
				new ResultListener<DirectoriesAndFiles>() {

					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					public void onSuccess(DirectoriesAndFiles result) {
						listener.onSuccess(new DirectoryContent(
								FileSystemItem.createFromDirectories(result
										.getDirectories()), FileSystemItem
										.createFromFiles(result.getFiles())));
					}
				});
	}

	public void getFileDetails(File item,
			ResultListener<FileDetails> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get file details: " + item.getId());

		service.doGetRequest(getUrl(item, FileAction.details), resultListener);
	}

	public void getDirectoryDetails(Directory item,
			ResultListener<DirectoryDetails> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get folder details: " + item.getId());

		service.doGetRequest(getUrl(item, FileAction.details), resultListener);
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Rename " + item.getId() + " to [" + newName + "]");

		service.doGetRequest(getUrl(FileAction.rename, item, new UrlParam("to",
				newName, UrlParam.Encoding.URL_FULL)), listener);
	}

	public void copy(File file, Directory directory,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Copy " + file.getId() + " to [" + directory.getId()
					+ "]");

		service.doGetRequest(getUrl(FileAction.copy, file, new UrlParam("to",
				directory.getId(), UrlParam.Encoding.URL_FULL)), listener);
	}

	public void move(FileSystemItem item, Directory directory,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Move " + item.getId() + " to [" + directory.getId()
					+ "]");

		service.doGetRequest(getUrl(FileAction.move, item, new UrlParam("to",
				directory.getId(), UrlParam.Encoding.URL_FULL)), listener);
	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Delete: " + item.getId());

		service.doGetRequest(getUrl(FileAction.delete, item), listener);
	}

	public void createDirectory(Directory parentFolder, String folderName,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Create directory: [" + folderName + "]");

		service.doGetRequest(getUrl(FileAction.create_folder, parentFolder,
				new UrlParam("name", folderName, UrlParam.Encoding.URL_FULL)),
				listener);
	}

	public String getDownloadUrl(File file) {
		return getUrl(FileAction.download, file);
	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		return getUrl(FileAction.download_as_zip, item);
	}

	public void setItemDescription(FileSystemItem item, String description,
			ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Set description: " + item.getId());

		service.doGetRequest(getUrl(FileAction.set_description, item,
				new UrlParam("description", description,
						UrlParam.Encoding.URL_FULL)), listener);
	}

	public void removeItemDescription(FileSystemItem item,
			ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Remove description: " + item.getId());

		service.doGetRequest(getUrl(FileAction.remove_description, item),
				listener);
	}

	public void getItemPermissions(FileSystemItem item,
			final ResultListener<List<FileItemUserPermission>> resultListener,
			final UserCache userCache, final FileSystemItemCache itemCache) {
		if (Log.isDebugEnabled())
			Log.debug("Get user permissions: " + item.getId());

		service.doGetRequest(getUrl(FileAction.get_item_permissions, item),
				new ResultListener<JsArray<JsFileItemUserPermission>>() {
					public void onFail(ServiceError error) {
						resultListener.onFail(error);
					}

					public void onSuccess(
							JsArray<JsFileItemUserPermission> result) {
						List<JsFileItemUserPermission> permissions = JsUtil
								.asList(result, JsFileItemUserPermission.class);
						resultListener.onSuccess(FileItemUserPermission
								.convert(permissions, userCache, itemCache));
					}
				});
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

		service.doPostRequest(getUrl(FileAction.update_item_permissions), data
				.toString(), resultListener);
	}

	private String getUrl(FileSystemItem item, FileAction action) {
		return service.getUrl(Arrays.asList(RequestType.filesystem.name(), item
				.getId(), action.name()), Collections.EMPTY_LIST);
	}

	public String getUrl(FileAction action, FileSystemItem item,
			UrlParam... params) {
		return null;
		// if (item.isEmpty()) {
		// throw new RuntimeException("No item defined, action "
		// + action.name());
		// }
		// return getUrl(action, item, Arrays.asList(params));
	}

	public String getUrl(FileAction action, FileSystemItem item,
			List<UrlParam> parameters) {
		return null;
		// List<UrlParam> params = new ArrayList(parameters);
		// params.add(new UrlParam("action", action.name()));
		// params.add(new UrlParam("id", item.getId()));
		//
		// return service.getUrl(RequestType.filesystem, params);
	}

	public String getUrl(FileAction action, UrlParam... parameters) {
		return null;
		// List<UrlParam> params = new ArrayList(Arrays.asList(parameters));
		// params.add(new UrlParam("action", action.name()));
		//
		// return service.getUrl(RequestType.filesystem, params);
	}
}
