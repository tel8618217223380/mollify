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
import org.sjarvela.mollify.client.file.DirectoryController;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.FileUploadListener;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.WindowManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public class MainViewPresenter implements DirectoryController,
		FileUploadListener {
	private final FileViewModel model;
	private final MainView view;
	private final WindowManager windowManager;
	private final FileActionProvider fileActionProvider;
	private FileUploadHandler fileUploadHandler;

	public MainViewPresenter(WindowManager windowManager, FileViewModel model,
			MainView view, FileActionProvider fileActionProvider,
			FileActionHandler fileActionHandler,
			FileUploadHandler fileUploadHandler) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.fileActionProvider = fileActionProvider;

		this.fileUploadHandler = fileUploadHandler;
		this.fileUploadHandler.addListener(this);

		fileActionHandler.addRenameListener(createRefreshListener());
		fileActionHandler.addDeleteListener(createRefreshListener());
	}

	public void initialize() {
		model.refreshRootDirectories(createListener(new ResultCallback() {
			public void onCallback(JavaScriptObject... result) {
				changeToRootDirectory(model.getRootDirectories().get(0));
			}
		}));
	}

	public void changeToRootDirectory(Directory root) {
		model.changeToRootDirectory(root, createRefreshListener());
	}

	public void changeToDirectory(Directory directory) {
		GWT.log("Directory changed to: " + directory.getName() + ", id="
				+ directory.getId(), null);
		model.changeToSubdirectory(directory, createRefreshListener());
	}

	public void reset() {
		view.clear();
		model.clear();
	}

	public void refresh() {
		view.refresh();
	}

	public void moveToParentDirectory() {
		if (!model.getDirectoryModel().canAscend())
			throw new RuntimeException("Cannot ascend");
		model.moveToParentDirectory(createRefreshListener());
	}

	public void changeToDirectory(int level, Directory directory) {
		model.changeToDirectory(level, directory, createRefreshListener());
	}

	public void onError(ServiceError error) {
		windowManager.getDialogManager().showError(error);
		reset();
	}

	public void openUploadDialog() {
		if (model.getCurrentFolder().isEmpty())
			return;
		windowManager.getDialogManager()
				.openUploadDialog(model.getCurrentFolder(), fileActionProvider,
						fileUploadHandler);
	}

	public void onUploadStarted() {

	}

	public void onUploadFinished() {
		refresh();
	}

	public void onUploadFailed(ServiceError error) {
		onError(error);
	}

	private ResultListener createListener(final ResultCallback callback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				onError(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				callback.onCallback(result);
			}
		};
	}

	private ResultListener createRefreshListener() {
		return createListener(new ResultCallback() {
			public void onCallback(JavaScriptObject... result) {
				refresh();
			}
		});
	}
}
