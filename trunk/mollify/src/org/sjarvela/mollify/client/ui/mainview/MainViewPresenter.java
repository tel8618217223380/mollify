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
import org.sjarvela.mollify.client.file.DirectoryController;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.WindowManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class MainViewPresenter implements DirectoryController {
	private final FileViewModel model;
	private final MainView view;

	private final WindowManager windowManager;
	private final FileServices fileOperator;

	public MainViewPresenter(MollifyService service,
			WindowManager windowManager, FileViewModel model, MainView view,
			FileServices fileOperator) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.fileOperator = fileOperator;

		view.getDirectorySelector().initialize(fileOperator, this);
	}

	public void onRefreshRootDirectories() {
		fileOperator
				.getRootDirectories(createDefaultListener(new ResultCallback() {
					public void onCallback(JavaScriptObject... result) {
						JsArray<Directory> roots = result[0].cast();
						onUpdateRootDirs(roots);
					}
				}));
	}

	private void onUpdateRootDirs(JsArray<Directory> rootDirs) {
		model.setRootDirectories(rootDirs);

		// select first one if none was selected
		if (rootDirs.length() > 0
				&& model.getDirectoryModel().getRootDirectory().isEmpty()) {
			model.getDirectoryModel().setRootDirectory(rootDirs.get(0));
			refresh();
		}
	}

	public void changeRootDirectory(Directory root) {
		model.getDirectoryModel().setRootDirectory(root);
		refresh();
	}

	public void changeDirectory(Directory directory) {
		GWT.log("Directory changed to: " + directory.getName() + ", id="
				+ directory.getId(), null);
		model.getDirectoryModel().descendIntoFolder(directory);
		refresh();
	}

	public void reset() {
		view.clear();
		model.clear();
	}

	public void refresh() {
		final String folder = model.getDirectoryModel().getCurrentFolder()
				.getId();

		fileOperator.getDirectoriesAndFiles(folder,
				createDefaultListener(new ResultCallback() {
					public void onCallback(JavaScriptObject... result) {
						JsArray<Directory> directories = result[0].cast();
						JsArray<File> files = result[1].cast();
						onRefreshList(directories, files);
					}
				}));
	}

	private void onRefreshList(JsArray<Directory> directories,
			JsArray<File> files) {
		model.setData(directories, files);
		view.refresh();
	}

	public void moveToParentDirectory() {
		if (!model.getDirectoryModel().canAscend())
			throw new RuntimeException("Cannot ascend");
		model.getDirectoryModel().ascend();
		refresh();
	}

	public void changeDirectory(int level, Directory directory) {
		model.getDirectoryModel().changeDirectory(level, directory);
		refresh();
	}
	
	public void onError(ServiceError error) {
		windowManager.getDialogManager().showError(error);
		reset();
	}

	public boolean isUploadAllowed() {
		if (model.getDirectoryModel().getCurrentFolder().isEmpty())
			return false;
		return true;
	}

	// private ResultListener createRefreshListener() {
	// return createDefaultListener(new ResultCallback() {
	// public void onCallback(JavaScriptObject... result) {
	// refresh();
	// }
	// });
	// }

	private ResultListener createDefaultListener(final ResultCallback callback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				windowManager.getDialogManager().showError(error);
				reset();
			}

			public void onSuccess(JavaScriptObject... result) {
				callback.onCallback(result);
			}
		};
	}
}
