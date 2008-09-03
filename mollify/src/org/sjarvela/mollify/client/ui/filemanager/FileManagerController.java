/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filemanager;

import org.sjarvela.mollify.client.DirectoryController;
import org.sjarvela.mollify.client.DirectoryProvider;
import org.sjarvela.mollify.client.FileAction;
import org.sjarvela.mollify.client.FileHandler;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.SuccessResult;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.fileaction.FileActionProvider;
import org.sjarvela.mollify.client.ui.filelist.Column;
import org.sjarvela.mollify.client.ui.filelist.SimpleFileListListener;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;

public class FileManagerController implements SimpleFileListListener,
		DirectoryController, DirectoryProvider, FileActionProvider, FileHandler {
	private MollifyService service;
	private FileManagerModel model;
	private FileManagerView view;

	public FileManagerController(MollifyService service,
			FileManagerModel model, FileManagerView view) {
		this.model = model;
		this.view = view;
		this.service = service;

		view.setDirectoryController(this);
		view.addFileListListener(this);
		view.setDirectoryProvider(this);
		view.setFileActionProvider(this);
		view.setFileHandler(this);
	}

	public void initialize() {
		getRootDirectories();
	}

	public void getRootDirectories() {
		this.service.getRootDirectories(new ResultListener() {

			public void onError(ServiceError error) {
				view.showError(error);
				reset();
			}

			public void onSuccess(JavaScriptObject result) {
				JsArray<Directory> dirs = result.cast();
				model.setRootDirectories(dirs);

				// select first one if none was selected
				if (dirs.length() > 0
						&& model.getDirectoryModel().getRootDirectory()
								.isEmpty()) {
					model.getDirectoryModel().setRootDirectory(dirs.get(0));
					refresh();
				}
			}
		});
	}

	public void changeRootDirectory(Directory root) {
		model.getDirectoryModel().setRootDirectory(root);
		refresh();
	}

	public void reset() {
		view.clear();
		model.clear();
	}

	public void refresh() {
		final String folder = model.getDirectoryModel().getCurrentFolder()
				.getId();

		this.service.getDirectories(new ResultListener() {
			public void onError(ServiceError error) {
				view.showError(error);
				reset();
			}

			public void onSuccess(JavaScriptObject result) {
				final JsArray<Directory> directories = result.cast();

				service.getFiles(new ResultListener() {
					public void onError(ServiceError error) {
						view.showError(error);
						reset();
					}

					public void onSuccess(JavaScriptObject result) {
						JsArray<File> files = result.cast();
						model.setData(directories, files);
						view.refresh();
					}
				}, folder);
			}
		}, folder);
	}

	public void onDirectoryRowClicked(Directory directory, Column column) {
		if (column.equals(Column.NAME)) {
			model.getDirectoryModel().descendIntoFolder(directory);
			refresh();
		}
	}

	public void onDirectoryUpRowClicked(Column column) {
		moveToParentDirectory();
	}

	public void moveToParentDirectory() {
		if (!model.getDirectoryModel().canAscend())
			throw new RuntimeException("Cannot ascend");
		model.getDirectoryModel().ascend();
		refresh();
	}

	public void onFileRowClicked(File file, Column column) {
		if (column.equals(Column.NAME)) {
			view.showFileAction(file);
		}
	}

	public void changeDirectory(int level, Directory directory) {
		model.getDirectoryModel().changeDirectory(level, directory);
		refresh();
	}

	public void getDirectories(Directory parent, final ResultListener listener) {
		// if there is no parent, show root list
		if (parent.isEmpty()) {
			listener.onSuccess(model.getRootDirectories());
			return;
		}

		// no need to retrieve current view directories, they are already
		// retrieved
		if (parent.equals(model.getDirectoryModel().getCurrentFolder())) {
			listener.onSuccess(model.getDirectories());
			return;
		}

		// proceed with ajax request
		this.service.getDirectories(listener, parent.getId());
	}

	public String getActionURL(File file, FileAction action) {
		return service.getFileActionUrl(file, action);
	}

	public boolean isActionAllowed(File file, FileAction action) {
		// TODO users rights
		return true;
	}

	public void onFileAction(File file, FileAction action) {
		if (action.equals(FileAction.DOWNLOAD)) {
			view.openDownloadUrl(this.getActionURL(file, action));
		} else if (action.equals(FileAction.RENAME)) {
			view.showRenameDialog(file);
		} else {
			Window.alert(action.name());
		}
	}

	public void onRename(File file, String newName) {
		service.renameFile(file, newName, new ResultListener() {

			public void onError(ServiceError error) {
				view.showError(error);
				refresh();
			}

			public void onSuccess(JavaScriptObject result) {
				SuccessResult success = result.cast();
				if (!success.isSuccess()) {
					Window.alert(success.getMessage()); // TODO proper message
					// dialog
				}
				refresh();
			}
		});
	}

}
