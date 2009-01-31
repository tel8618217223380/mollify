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

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.data.JsDirectory;
import org.sjarvela.mollify.client.file.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.file.DirectoryHandler;
import org.sjarvela.mollify.client.file.FileDetailsProvider;
import org.sjarvela.mollify.client.file.FileOperationHandler;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FileServices implements FileDetailsProvider, FileOperationHandler,
		DirectoryHandler, DirectoryDetailsProvider {
	private final MollifyService service;

	public FileServices(MollifyService service) {
		this.service = service;
	}

	public void getDirectories(Directory parent, final ResultListener listener) {
		this.service.getDirectories(new ResultListener() {

			public void onFail(MollifyError error) {
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
		this.service.getRootDirectories(new ResultListener() {

			public void onFail(MollifyError error) {
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
		this.service.getDirectoriesAndFiles(new ResultListener() {

			public void onFail(MollifyError error) {
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
		service.getFileDetails(file, resultListener);
	}

	public void getDirectoryDetails(Directory directory,
			ResultListener resultListener) {
		service.getDirectoryDetails(directory, resultListener);
	}

	public void onRename(FileSystemItem item, String newName,
			ResultListener listener) {
		if (item.isFile())
			service.renameFile((File) item, newName, listener);
		else
			service.renameDirectory((Directory) item, newName, listener);
	}

	public void onDelete(FileSystemItem item, ResultListener listener) {
		if (item.isFile())
			service.deleteFile((File) item, listener);
		else
			throw new RuntimeException("Not implemented yet");
	}

	public void onCreateDirectory(Directory parentFolder, String folderName,
			ResultListener listener) {
		service.createFolder(parentFolder, folderName, listener);
	}

}
