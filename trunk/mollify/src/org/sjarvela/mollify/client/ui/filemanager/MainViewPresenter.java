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
import org.sjarvela.mollify.client.FileDetailsProvider;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class MainViewPresenter implements DirectoryController,
		DirectoryProvider, FileDetailsProvider {
	private MollifyService service;
	private MainViewModel model;
	private MainView view;

	public MainViewPresenter(MollifyService service, MainViewModel model,
			MainView view) {
		this.model = model;
		this.view = view;
		this.service = service;

		view.setDirectoryController(this);
		view.setDirectoryProvider(this);
	}

	public void initialize() {
		this.service.checkAuthentication(new ResultListener() {
			public void onFail(ServiceError error) {
				if (ServiceError.AUTHENTICATION_FAILED.equals(error)) {
					showLogin();
					return;
				}
				view.showError(error);
				reset();
			}

			public void onSuccess(JavaScriptObject result) {
				getRootDirectories();
			}
		});
	}

	private void showLogin() {
		view.showInfo("Login", "Login here");
	}

	public void getRootDirectories() {
		this.service.getRootDirectories(new ResultListener() {
			public void onFail(ServiceError error) {
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

	public void changeDirectory(Directory directory) {
		model.getDirectoryModel().descendIntoFolder(directory);
		view.refresh();
	}
	
	public void reset() {
		view.clear();
		model.clear();
	}

	public void refresh() {
		final String folder = model.getDirectoryModel().getCurrentFolder()
				.getId();

		this.service.getDirectories(new ResultListener() {
			public void onFail(ServiceError error) {
				view.showError(error);
				reset();
			}

			public void onSuccess(JavaScriptObject result) {
				final JsArray<Directory> directories = result.cast();

				service.getFiles(new ResultListener() {
					public void onFail(ServiceError error) {
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

		this.service.getDirectories(listener, parent.getId());
	}

	// private void onOperationFailed(ErrorValue result) {
	// GWT.log(result.getError(), null);
	// view.showError(result);
	// }

	public void getFileDetails(File file, ResultListener resultListener) {
		service.getFileDetails(file, resultListener);
	}


}
