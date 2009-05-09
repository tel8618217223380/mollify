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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.DirectoriesAndFiles;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.DirectoryDetails;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.UrlParam;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;

public class PhpFileService implements FileSystemService {
	protected final PhpService service;

	enum FileDataAction {
		files, directories, contents, details, upload_status
	};

	public PhpFileService(PhpService service) {
		this.service = service;
	}

	public void getDirectories(Directory parent,
			final ResultListener<List<Directory>> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories: " + parent.getId());

		service.doRequest(getFileDataUrl(FileDataAction.directories,
				new UrlParam("dir", parent.getId())),
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

		service.doRequest(getFileDataUrl(FileDataAction.contents, new UrlParam(
				"dir", parent.getId())),
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

		service.doRequest(getFileDataUrl(FileDataAction.details,
				getFileItemTypeParam(item), getFileItemIdParam(item)),
				resultListener);
	}

	public void getDirectoryDetails(Directory item,
			ResultListener<DirectoryDetails> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get folder details: " + item.getId());

		service.doRequest(getFileDataUrl(FileDataAction.details,
				getFileItemTypeParam(item), getFileItemIdParam(item)),
				resultListener);
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Rename " + item.getId() + " to [" + newName + "]");

		service.doRequest(getFileActionUrl(item, FileSystemAction.rename,
				new UrlParam("to", newName, UrlParam.Encoding.URL_FULL)),
				listener);
	}

	public void copy(File file, Directory directory,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Copy " + file.getId() + " to [" + directory.getId()
					+ "]");

		service.doRequest(getFileActionUrl(file, FileSystemAction.copy,
				new UrlParam("to", directory.getId(),
						UrlParam.Encoding.URL_FULL)), listener);
	}

	public void move(FileSystemItem item, Directory directory,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Move " + item.getId() + " to [" + directory.getId()
					+ "]");

		service.doRequest(getFileActionUrl(item, FileSystemAction.move,
				new UrlParam("to", directory.getId(),
						UrlParam.Encoding.URL_FULL)), listener);
	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Delete: " + item.getId());

		service.doRequest(getFileActionUrl(item, FileSystemAction.delete),
				listener);
	}

	public void createDirectory(Directory parentFolder, String folderName,
			ResultListener<Boolean> listener) {
		if (Log.isDebugEnabled())
			Log.debug("Create directory: [" + folderName + "]");

		service.doRequest(getFileActionUrl(parentFolder,
				FileSystemAction.create_folder, new UrlParam("name",
						folderName, UrlParam.Encoding.URL_FULL)), listener);
	}

	public String getDownloadUrl(File file) {
		return getFileActionUrl(file, FileSystemAction.download);
	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		return getFileActionUrl(item, FileSystemAction.download_as_zip);
	}

	public String getFileActionUrl(FileSystemItem item,
			FileSystemAction action, UrlParam... params) {
		return getFileActionUrl(item, action, Arrays.asList(params));
	}

	public String getFileActionUrl(FileSystemItem item,
			FileSystemAction action, List<UrlParam> parameters) {
		if (item.isEmpty()) {
			throw new RuntimeException("No item defined, action "
					+ action.name());
		}
		if (!action.isApplicable(item)) {
			throw new RuntimeException("Invalid action request "
					+ action.name());
		}

		List<UrlParam> params = new ArrayList(parameters);
		params.add(new UrlParam("action", action.name()));
		params.add(new UrlParam("id", item.getId()));
		params.add(getFileItemTypeParam(item));

		return service.getUrl(RequestType.file_action, params);
	}

	protected String getFileDataUrl(FileDataAction action, UrlParam... params) {
		return getFileDataUrl(action, Arrays.asList(params));
	}

	public void setItemDescription(FileSystemItem item, String description,
			ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Set description: " + item.getId());

		service.doRequest(getFileActionUrl(item,
				FileSystemAction.set_description, new UrlParam("description",
						description, UrlParam.Encoding.URL_FULL)), listener);
	}

	public void removeItemDescription(FileSystemItem item,
			ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Remove description: " + item.getId());

		service.doRequest(getFileActionUrl(item,
				FileSystemAction.remove_description), listener);
	}

	private String getFileDataUrl(FileDataAction action,
			List<UrlParam> parameters) {
		List<UrlParam> params = new ArrayList(parameters);
		params.add(0, new UrlParam("action", action.name()));
		return service.getUrl(RequestType.file_data, params);
	}

	public static UrlParam getFileItemIdParam(FileSystemItem item) {
		return new UrlParam("id", item.getId());
	}

	public static UrlParam getFileItemTypeParam(FileSystemItem item) {
		return new UrlParam("item_type", (item.isFile() ? "f" : "d"),
				UrlParam.Encoding.NONE);
	}
}
