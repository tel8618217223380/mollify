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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.provider.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.filesystem.provider.FileDetailsProvider;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public interface FileSystemService extends FileDetailsProvider,
		DirectoryDetailsProvider {

	void getDirectories(Directory parent,
			ResultListener<List<Directory>> listener);

	void getDirectoryContents(Directory parent,
			ResultListener<DirectoryContent> listener);

	void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener);

	void copy(File file, Directory directory, ResultListener<Boolean> listener);

	void move(FileSystemItem file, Directory toDirectory,
			ResultListener<Boolean> listener);

	void delete(FileSystemItem item, ResultListener<Boolean> listener);

	void createDirectory(Directory parentFolder, String folderName,
			ResultListener<Boolean> resultListener);

	String getDownloadUrl(File file);

	String getDownloadAsZipUrl(FileSystemItem item);

	void setItemDescription(FileSystemItem item, String description,
			ResultListener listener);

	void removeItemDescription(FileSystemItem item, ResultListener listener);
}