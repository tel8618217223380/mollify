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
import org.sjarvela.mollify.client.file.FileDetailsProvider;
import org.sjarvela.mollify.client.file.FileOperationHandler;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FileServices implements FileDetailsProvider, FileOperationHandler {
	private final MollifyService service;

	public FileServices(MollifyService service) {
		this.service = service;
	}

	public void getDirectories(Directory parent, ResultListener listener) {
		this.service.getDirectories(listener, parent.getId());
	}

	public void getRootDirectories(ResultListener listener) {
		this.service.getRootDirectories(listener);
	}

	public void getDirectoriesAndFiles(final String folder,
			final ResultListener listener) {
		this.service.getDirectories(new ResultListener() {

			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				final JsArray<Directory> directories = result[0].cast();
				service.getFiles(new ResultListener() {

					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					public void onSuccess(JavaScriptObject... result) {
						JsArray<File> files = result[0].cast();
						listener.onSuccess(directories, files);
					}
				}, folder);
			}

		}, folder);
	}

	public void getFileDetails(File file, ResultListener resultListener) {
		service.getFileDetails(file, resultListener);
	}

	public void onRename(File file, String newName, ResultListener listener) {
		service.renameFile(file, newName, listener);
	}

	public void onDelete(File file, ResultListener listener) {
		service.deleteFile(file, listener);
	}

}
