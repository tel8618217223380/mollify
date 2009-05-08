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

import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryModel;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;

public class MainViewModel {
	private final SessionInfo session;
	private final FileSystemService fileServices;
	private final List<Directory> rootDirectories;

	private DirectoryModel directoryModel;
	private List<File> files = new ArrayList();
	private List<Directory> directories = new ArrayList();
	private List<FileSystemItem> all = new ArrayList();

	public MainViewModel(FileSystemService fileServices, SessionInfo session,
			DirectoryProvider directoryProvider) {
		this.fileServices = fileServices;
		this.session = session;
		this.rootDirectories = directoryProvider.getRootDirectories();

		clear();
	}

	public void clear() {
		directoryModel = new DirectoryModel();

		directories.clear();
		files.clear();
		all.clear();
	}

	public SessionInfo getSession() {
		return session;
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

	public boolean hasFolder() {
		return directoryModel.getCurrentFolder() != null;
	}

	public Directory getCurrentFolder() {
		return directoryModel.getCurrentFolder();
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
		if (getCurrentFolder() == null) {
			resultListener.onSuccess(new DirectoryContent());
			return;
		}

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
