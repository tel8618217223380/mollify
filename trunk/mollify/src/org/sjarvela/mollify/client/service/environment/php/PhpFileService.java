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

import org.sjarvela.mollify.client.filesystem.DirectoriesAndFiles;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.js.JsDirectory;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.ResultListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class PhpFileService implements FileSystemService {
	private final PhpService service;

	public PhpFileService(PhpService service) {
		this.service = service;
	}

	public void getDirectories(Directory parent, final ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories: " + parent.getId());

		this.service.getDirectories(new ResultListener() {

			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(Object... result) {
				listener
						.onSuccess(FileSystemItem
								.createFromDirectories((JsArray<JsDirectory>) result[0]));
			}
		}, parent.getId());
	}

	public void getRootDirectories(final ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get root directories");

		this.service.getRootDirectories(new ResultListener() {

			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(Object... result) {
				listener
						.onSuccess(FileSystemItem
								.createFromDirectories((JsArray<JsDirectory>) result[0]));
			}
		});
	}

	public void getDirectoriesAndFiles(final String folder,
			final ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directory contents: " + folder);

		this.service.getDirectoriesAndFiles(new ResultListener() {

			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(Object... result) {
				DirectoriesAndFiles dirsAndFiles = ((JavaScriptObject) result[0])
						.cast();
				listener
						.onSuccess(FileSystemItem
								.createFromDirectories(dirsAndFiles
										.getDirectories()), FileSystemItem
								.createFromFiles(dirsAndFiles.getFiles()));
			}

		}, folder);
	}

	public void getFileDetails(File file, ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get file details: " + file.getId());

		service.getFileDetails(file, resultListener);
	}

	public void getDirectoryDetails(Directory directory,
			ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directory details: " + directory.getId());

		service.getDirectoryDetails(directory, resultListener);
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Rename " + item.getId() + " to [" + newName + "]");

		if (item.isFile())
			service.renameFile((File) item, newName, listener);
		else
			service.renameDirectory((Directory) item, newName, listener);
	}

	public void delete(FileSystemItem item, ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Delete: " + item.getId());

		if (item.isFile())
			service.deleteFile((File) item, listener);
		else
			service.deleteDirectory((Directory) item, listener);
	}

	public void createDirectory(Directory parentFolder, String folderName,
			ResultListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("Create directory: [" + folderName + "]");
		service.createFolder(parentFolder, folderName, listener);
	}

	public String getDownloadUrl(File file) {
		return service.getActionUrl(file, FileSystemAction.download);
	}

}
