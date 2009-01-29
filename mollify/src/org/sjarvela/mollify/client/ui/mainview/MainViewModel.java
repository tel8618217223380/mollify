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
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.data.SessionInfo;
import org.sjarvela.mollify.client.file.DirectoryModel;
import org.sjarvela.mollify.client.service.FileServices;
import org.sjarvela.mollify.client.service.MollifyError;
import org.sjarvela.mollify.client.service.ResultListener;

public class MainViewModel {
	private final SessionInfo info;
	private final FileServices fileServices;

	private List<Directory> rootDirectories;
	private List<Directory> directories;
	private List<File> files;
	private List<FileSystemItem> all;

	private DirectoryModel directoryModel;

	public MainViewModel(FileServices fileServices, SessionInfo info) {
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
				new ResultCallback() {
					public void onCallback(Object... result) {
						MainViewModel.this.rootDirectories = (List<Directory>) result[0];
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

	public void refreshData(ResultListener resultListener) {
		final String folder = getCurrentFolder().getId();

		fileServices.getDirectoriesAndFiles(folder, createListener(
				resultListener, new ResultCallback() {
					public void onCallback(Object... result) {
						onUpdateData((List<Directory>) result[0],
								(List<File>) result[1]);
					}
				}));
	}

	private void onUpdateData(List<Directory> dirs, List<File> files) {
		this.directories = dirs;
		this.files = files;
		this.all = new ArrayList(dirs);
		this.all.addAll(files);
	}

	private ResultListener createListener(final ResultListener listener,
			final ResultCallback resultCallback) {
		return new ResultListener() {
			public void onFail(MollifyError error) {
				listener.onFail(error);
			}

			public void onSuccess(Object... result) {
				resultCallback.onCallback(result);
				listener.onSuccess(result);
			}

		};
	}

}
