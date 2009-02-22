/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.demo;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.data.FileUploadStatus;
import org.sjarvela.mollify.client.file.FileSystemAction;
import org.sjarvela.mollify.client.log.MollifyLogger;
import org.sjarvela.mollify.client.service.DirectoriesAndFiles;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;

public class DemoMollifyService implements MollifyService {
	private static final String MOLLIFY_PACKAGE_URL = "http://mollify.googlecode.com/files/mollify_0.7.1.tar.gz";

	private MollifyLogger logger;
	private DemoData data;

	public void initialize(MollifyLogger logger, String path) {
		this.logger = logger;
		this.logger.logInfo("Mollify Demo");
		this.data = new DemoData();
	}

	public void authenticate(String userName, String password,
			ResultListener resultListener) {
		resultListener.onSuccess(data.getSessionInfo(userName));
	}

	public void createFolder(Directory parentFolder, String folderName,
			ResultListener listener) {
		listener.onSuccess(true);
	}

	public void deleteDirectory(Directory dir, ResultListener listener) {
		listener.onSuccess(true);
	}

	public void deleteFile(File file, ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public String getActionUrl(FileSystemItem item, FileSystemAction action) {
		return getActionUrl(item, action, new String[0]);
	}

	public String getActionUrl(FileSystemItem item, FileSystemAction action,
			String... params) {
		if (action.equals(FileSystemAction.download)
				|| action.equals(FileSystemAction.download_as_zip))
			return MOLLIFY_PACKAGE_URL;
		return "";
	}

	public void getDirectories(ResultListener resultListener, String dir) {
		resultListener.onSuccess(data.getDirectories(dir));
	}

	public void getDirectoriesAndFiles(ResultListener resultListener, String dir) {
		resultListener.onSuccess(DirectoriesAndFiles.create(data
				.getDirectories(dir), data.getFiles(dir)));
	}

	public void getDirectoryDetails(Directory directory,
			ResultListener resultListener) {
		resultListener.onSuccess(data.getDirectoryDetails(directory));
	}

	public void getFileDetails(File file, ResultListener resultListener) {
		resultListener.onSuccess(data.getFileDetails(file));
	}

	public void getFiles(ResultListener resultListener, String dir) {
		resultListener.onSuccess(data.getFiles(dir));
	}

	public String getNewUploadId() {
		return "";
	}

	public void getRootDirectories(ResultListener resultListener) {
		resultListener.onSuccess(data.getRootDirectories());
	}

	public void getSessionInfo(ResultListener resultListener) {
		resultListener.onSuccess(data.getSessionInfo(""));
	}

	public void getUploadProgress(String id, ResultListener resultListener) {
		resultListener.onSuccess(FileUploadStatus.create(100, 100, 100,
				"Example.txt", 1));
	}

	public void logout(ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

	public void renameDirectory(Directory dir, String newName,
			ResultListener listener) {
		listener.onSuccess(true);
	}

	public void renameFile(File file, String newName,
			ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

}
