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

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.FileUploadStatus;
import org.sjarvela.mollify.client.file.DirectoryController;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.FileUploadListener;
import org.sjarvela.mollify.client.file.FileUploadMonitor;
import org.sjarvela.mollify.client.file.FileUploadProgressListener;
import org.sjarvela.mollify.client.file.ProgressListener;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.WindowManager;

import com.google.gwt.core.client.JavaScriptObject;

public class MainViewPresenter implements DirectoryController,
		FileUploadListener {
	private final MainViewModel model;
	private final MainView view;
	private final WindowManager windowManager;
	private final FileActionProvider fileActionProvider;
	private final Localizator localizator;
	private final FileUploadHandler fileUploadHandler;
	private ProgressListener uploadListener = null;
	private FileUploadMonitor uploadMonitor;

	public MainViewPresenter(WindowManager windowManager, MainViewModel model,
			MainView view, FileActionProvider fileActionProvider,
			FileActionHandler fileActionHandler,
			FileUploadHandler fileUploadHandler, Localizator localizator) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.fileActionProvider = fileActionProvider;

		this.fileUploadHandler = fileUploadHandler;
		this.localizator = localizator;
		this.fileUploadHandler.addListener(this);

		fileActionHandler.addRenameListener(createRefreshListener());
		fileActionHandler.addDeleteListener(createRefreshListener());
	}

	public void initialize() {
		model.refreshRootDirectories(createListener(new Callback() {
			public void onCallback() {
				changeToRootDirectory(model.getRootDirectories().get(0));
			}
		}));
	}

	public void changeToRootDirectory(Directory root) {
		model.changeToRootDirectory(root, createRefreshListener());
	}

	public void changeToDirectory(Directory directory) {
		model.changeToSubdirectory(directory, createRefreshListener());
	}

	public void reset() {
		view.clear();
		model.clear();
	}

	public void refresh() {
		model.refreshData(createListener(new Callback() {
			public void onCallback() {
				view.refresh();
			}
		}));
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

	public void onUploadStarted(String uploadId, String fileName) {
		if (uploadListener != null || uploadMonitor != null)
			throw new RuntimeException("Previous upload unfinished");

		uploadListener = windowManager.getDialogManager().openProgressDialog(
				localizator.getStrings().fileUploadProgressTitle(), false);
		uploadListener.setInfo(fileName);
		uploadListener.setDetails(localizator.getStrings()
				.fileUploadProgressPleaseWait());

		uploadMonitor = new FileUploadMonitor(uploadId,
				new FileUploadProgressListener() {
					public void onProgressUpdate(FileUploadStatus status) {
						int percentage = (int) status.getUploadedPercentage();
						uploadListener.setProgressBarVisible(true);
						uploadListener.setProgress(percentage);
						uploadListener.setDetails(String.valueOf(percentage)
								+ "%");
					}

					public void onProgressUpdateFail(ServiceError error) {
						uploadListener.setProgress(0);
						uploadMonitor.stop();
					}
				}, fileUploadHandler);
		uploadMonitor.start();
	}

	public void onUploadFinished() {
		stopUploaders();
		refresh();
	}

	public void onUploadFailed(ServiceError error) {
		stopUploaders();
		onError(error);
	}

	private void stopUploaders() {
		if (uploadListener != null) {
			uploadListener.setProgress(100);
			uploadListener.setDetails("");
			uploadListener.onFinished();
		}
		if (uploadMonitor != null) {
			uploadMonitor.stop();
		}
		uploadListener = null;
		uploadMonitor = null;
	}

	private ResultListener createListener(final Callback callback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				onError(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				callback.onCallback();
			}
		};
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
