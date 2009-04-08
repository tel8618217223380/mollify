/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryModel;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;

public class MainViewModel {
	private final SessionInfo info;
	private final FileSystemService fileServices;

	private List<Directory> rootDirectories;
	private List<Directory> directories;
	private List<File> files;
	private List<FileSystemItem> all;

	private DirectoryModel directoryModel;

	public MainViewModel(FileSystemService fileServices, SessionInfo info) {
		this.fileServices = fileServices;
		this.info = info;
		clear();
	}

	public void clear() {
		rootDirectories = new ArrayList();
		directories = new ArrayList();
		files = new ArrayList();
		all = new ArrayList();
		directoryModel = new DirectoryModel();
	}

	public SessionInfo getSessionInfo() {
		return info;
	}

	public DirectoryModel getDirectoryModel() {
		return directoryModel;
	}

	public List<Directory> getRootDirectories() {
		return rootDirectories;
	}

	public List<Directory> getSubDirectories() {
		return directories;
	}

	public List<File> getFiles() {
		return files;
	}

	public List<FileSystemItem> getAllItems() {
		return all;
	}

	public Directory getCurrentFolder() {
		return directoryModel.getCurrentFolder();
	}

	public void refreshRootDirectories(ResultListener listener) {
		fileServices.getRootDirectories(createListener(listener,
				new ResultCallback<List<Directory>>() {
					public void onCallback(List<Directory> result) {
						MainViewModel.this.rootDirectories = result;
					}
				}));
	}

	public void changeToRootDirectory(Directory root,
			ResultListener resultListener) {
		directoryModel.setRootDirectory(root);
		refreshData(resultListener);
	}

	public void changeToSubdirectory(Directory directory,
			ResultListener resultListener) {
		directoryModel.descendIntoFolder(directory);
		refreshData(resultListener);
	}

	public void changeToDirectory(int level, Directory directory,
			ResultListener resultListener) {
		directoryModel.changeDirectory(level, directory);
		refreshData(resultListener);
	}

	public void moveToParentDirectory(ResultListener resultListener) {
		directoryModel.ascend();
		refreshData(resultListener);
	}

	public void refreshData(ResultListener<DirectoryContent> resultListener) {
		fileServices.getDirectoryContents(getCurrentFolder(), createListener(
				resultListener, new ResultCallback<DirectoryContent>() {
					public void onCallback(DirectoryContent result) {
						onUpdateData(result);
					}
				}));
	}

	private void onUpdateData(DirectoryContent data) {
		this.directories = data.getDirectories();
		this.files = data.getFiles();
		this.all = new ArrayList(data.getDirectories());
		this.all.addAll(files);
	}

	private ResultListener createListener(final ResultListener listener,
			final ResultCallback resultCallback) {
		return new ResultListener<Object>() {
			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(Object result) {
				resultCallback.onCallback(result);
				listener.onSuccess(result);
			}
		};
	}
}
