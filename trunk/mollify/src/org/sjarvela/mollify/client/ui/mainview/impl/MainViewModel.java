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

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderModel;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ResultCallback;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.file.FilePermission;

public class MainViewModel {
	private final SessionInfo session;
	private final FileSystemService fileServices;
	private final List<Folder> rootDirectories;

	private FolderModel folderModel;
	private List<File> files = new ArrayList();
	private List<Folder> folders = new ArrayList();
	private List<FileSystemItem> all = new ArrayList();
	private List<FileSystemItem> selected = new ArrayList();
	private FilePermission folderPermission = FilePermission.None;

	public MainViewModel(FileSystemService fileServices, SessionInfo session,
			FolderProvider directoryProvider) {
		this.fileServices = fileServices;
		this.session = session;
		this.rootDirectories = directoryProvider.getRootFolders();

		clear();
	}

	public void clear() {
		folderModel = new FolderModel();

		folders.clear();
		files.clear();
		all.clear();
	}

	public SessionInfo getSession() {
		return session;
	}

	public FolderModel getFolderModel() {
		return folderModel;
	}

	public List<Folder> getRootDirectories() {
		return rootDirectories;
	}

	public List<Folder> getSubDirectories() {
		return folders;
	}

	public FilePermission getFolderPermission() {
		return folderPermission;
	}

	public List<File> getFiles() {
		return files;
	}

	public List<FileSystemItem> getAllItems() {
		return all;
	}

	public boolean hasFolder() {
		return folderModel.getCurrentFolder() != null;
	}

	public Folder getCurrentFolder() {
		return folderModel.getCurrentFolder();
	}

	public void changeToRootDirectory(Folder root, ResultListener resultListener) {
		folderModel.setRootFolder(root);
		refreshData(resultListener);
	}

	public void changeToSubdirectory(Folder directory,
			ResultListener resultListener) {
		folderModel.descendIntoFolder(directory);
		refreshData(resultListener);
	}

	public void changeToDirectory(int level, Folder directory,
			ResultListener resultListener) {
		folderModel.changeDirectory(level, directory);
		refreshData(resultListener);
	}

	public void moveToParentDirectory(ResultListener resultListener) {
		folderModel.ascend();
		refreshData(resultListener);
	}

	public void refreshData(ResultListener<FolderInfo> resultListener) {
		if (getCurrentFolder() == null) {
			resultListener.onSuccess(new FolderInfo());
			return;
		}

		fileServices.getInfo(getCurrentFolder(), createListener(resultListener,
				new ResultCallback<FolderInfo>() {
					public void onCallback(FolderInfo result) {
						onUpdateData(result);
					}
				}));
	}

	private void onUpdateData(FolderInfo data) {
		this.folders = data.getFolders();
		this.files = data.getFiles();
		this.folderPermission = data.getPermission();
		this.all = new ArrayList(data.getFolders());
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

	public void setSelected(List<FileSystemItem> selected) {
		this.selected = selected;
	}

	public List<FileSystemItem> getSelectedItems() {
		return this.selected;
	}

	public void clearSelected() {
		this.selected.clear();
	}
}
