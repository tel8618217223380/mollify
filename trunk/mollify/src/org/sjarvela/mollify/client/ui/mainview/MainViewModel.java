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

import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.file.DirectoryModel;
import org.sjarvela.mollify.client.service.FileServices;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class MainViewModel {
	private final FileServices fileServices;

	private JsArray<Directory> rootDirectories;
	private JsArray<Directory> directories;
	private JsArray<File> files;

	private DirectoryModel directoryModel;

	public MainViewModel(FileServices fileServices) {
		this.fileServices = fileServices;
		clear();
	}

	void clear() {
		rootDirectories = JsArray.createArray().cast();
		directories = JsArray.createArray().cast();
		files = JsArray.createArray().cast();
		directoryModel = new DirectoryModel();
	}

	public DirectoryModel getDirectoryModel() {
		return directoryModel;
	}

	public JsArray<Directory> getRootDirectories() {
		return rootDirectories;
	}

	public JsArray<Directory> getSubDirectories() {
		return directories;
	}

	public JsArray<File> getFiles() {
		return files;
	}

	public Directory getCurrentFolder() {
		return directoryModel.getCurrentFolder();
	}

	public void refreshRootDirectories(ResultListener listener) {
		fileServices.getRootDirectories(createListener(listener,
				new ResultCallback() {
					public void onCallback(JavaScriptObject... result) {
						JsArray<Directory> rootDirs = result[0].cast();
						MainViewModel.this.rootDirectories = rootDirs;
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
					public void onCallback(JavaScriptObject... result) {
						JsArray<Directory> directories = result[0].cast();
						JsArray<File> files = result[1].cast();
						onUpdateData(directories, files);
					}
				}));
	}

	private void onUpdateData(JsArray<Directory> directories,
			JsArray<File> files) {
		this.directories = directories;
		this.files = files;
	}

	private ResultListener createListener(final ResultListener listener,
			final ResultCallback resultCallback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				resultCallback.onCallback(result);
				listener.onSuccess(result);
			}

		};
	}

}
