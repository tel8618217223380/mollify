/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.demo;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryDetails;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.request.ResultListener;

public class DemoFileService implements FileSystemService {
	private final DemoData data;

	public DemoFileService(DemoData data) {
		this.data = data;
	}

	public void createDirectory(Directory parentFolder, String folderName,
			ResultListener<Boolean> resultListener) {
		resultListener.onSuccess(true);
	}

	public void delete(FileSystemItem item, ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void getDirectories(Directory parent,
			ResultListener<List<Directory>> listener) {
		listener.onSuccess(data.getDirectories(parent));
	}

	public void getDirectoryContents(Directory parent,
			ResultListener<DirectoryContent> listener) {
		listener.onSuccess(new DirectoryContent(data.getDirectories(parent), data
				.getFiles(parent)));
	}

	public String getDownloadUrl(File file) {
		return DemoEnvironment.MOLLIFY_PACKAGE_URL;
	}

	public void rename(FileSystemItem item, String newName,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void copy(File file, Directory directory,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void move(File file, Directory directory,
			ResultListener<Boolean> listener) {
		listener.onSuccess(true);
	}

	public void getFileDetails(File file, ResultListener<FileDetails> listener) {
		listener.onSuccess(data.getFileDetails(file));
	}

	public void getDirectoryDetails(Directory directory,
			ResultListener<DirectoryDetails> resultListener) {
		resultListener.onSuccess(data.getDirectoryDetails(directory));
	}

	public void getRootDirectories(ResultListener<List<Directory>> listener) {
		listener.onSuccess(data.getRootDirectories());
	}

	public String getDownloadAsZipUrl(FileSystemItem item) {
		return DemoEnvironment.MOLLIFY_PACKAGE_URL;
	}

}
