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
import org.sjarvela.mollify.client.file.FileActionUrlProvider;
import org.sjarvela.mollify.client.file.FileSystemAction;
import org.sjarvela.mollify.client.log.MollifyLogger;

public interface MollifyService extends FileActionUrlProvider {
	void initialize(MollifyLogger logger, String parameter);

	void getSessionInfo(ResultListener resultListener);

	void authenticate(String userName, String password,
			final ResultListener resultListener);

	void logout(ResultListener resultListener);

	void getFiles(ResultListener resultListener, String dir);

	void getDirectories(ResultListener resultListener, String dir);

	void getDirectoriesAndFiles(ResultListener resultListener, String dir);

	void getRootDirectories(ResultListener resultListener);

	void getFileDetails(File file, ResultListener resultListener);

	void getDirectoryDetails(Directory directory, ResultListener resultListener);

	void renameFile(File file, String newName, ResultListener resultListener);

	void renameDirectory(Directory dir, String newName, ResultListener listener);

	void deleteFile(File file, ResultListener resultListener);

	void deleteDirectory(Directory dir, ResultListener listener);

	void createFolder(Directory parentFolder, String folderName,
			ResultListener listener);

	void getUploadProgress(String id, ResultListener resultListener);

	String getActionUrl(FileSystemItem item, FileSystemAction action);

	String getActionUrl(FileSystemItem item, FileSystemAction action,
			String... params);

	String getNewUploadId();

}