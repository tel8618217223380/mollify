/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderContent;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderModel;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ResultCallback;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;

public class MainViewModel {
	private final SessionInfo session;
	private final FileSystemService fileServices;
	private final List<Folder> rootDirectories;

	private FolderModel directoryModel;
	private List<File> files = new ArrayList();
	private List<Folder> directories = new ArrayList();
	private List<FileSystemItem> all = new ArrayList();

	public MainViewModel(FileSystemService fileServices, SessionInfo session,
			FolderProvider directoryProvider) {
		this.fileServices = fileServices;
		this.session = session;
		this.rootDirectories = directoryProvider.getRootFolders();

		clear();
	}

	public void clear() {
		directoryModel = new FolderModel();

		directories.clear();
		files.clear();
		all.clear();
	}

	public SessionInfo getSession() {
		return session;
	}

	public FolderModel getFolderModel() {
		return directoryModel;
	}

	public List<Folder> getRootDirectories() {
		return rootDirectories;
	}

	public List<Folder> getSubDirectories() {
		return directories;
	}

	public List<File> getFiles() {
		return files;
	}

	public List<FileSystemItem> getAllItems() {
		return all;
	}

	public boolean hasFolder() {
		return directoryModel.getCurrentFolder() != null;
	}

	public Folder getCurrentFolder() {
		return directoryModel.getCurrentFolder();
	}

	public void changeToRootDirectory(Folder root,
			ResultListener resultListener) {
		directoryModel.setRootFolder(root);
		refreshData(resultListener);
	}

	public void changeToSubdirectory(Folder directory,
			ResultListener resultListener) {
		directoryModel.descendIntoFolder(directory);
		refreshData(resultListener);
	}

	public void changeToDirectory(int level, Folder directory,
			ResultListener resultListener) {
		directoryModel.changeDirectory(level, directory);
		refreshData(resultListener);
	}

	public void moveToParentDirectory(ResultListener resultListener) {
		directoryModel.ascend();
		refreshData(resultListener);
	}

	public void refreshData(ResultListener<FolderContent> resultListener) {
		if (getCurrentFolder() == null) {
			resultListener.onSuccess(new FolderContent());
			return;
		}

		fileServices.getItems(getCurrentFolder(), createListener(
				resultListener, new ResultCallback<FolderContent>() {
					public void onCallback(FolderContent result) {
						onUpdateData(result);
					}
				}));
	}

	private void onUpdateData(FolderContent data) {
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
