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

import java.util.List;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.LogoutListener;
import org.sjarvela.mollify.client.ProgressListener;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.data.FileUploadStatus;
import org.sjarvela.mollify.client.file.DirectoryController;
import org.sjarvela.mollify.client.file.DirectoryHandler;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.FileUploadListener;
import org.sjarvela.mollify.client.file.FileUploadMonitor;
import org.sjarvela.mollify.client.file.FileUploadProgressListener;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyError;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.filelist.Column;

public class MainViewPresenter implements DirectoryController,
		FileUploadListener {
	private final MainViewModel model;
	private final MainView view;
	private final WindowManager windowManager;
	private final FileActionProvider fileActionProvider;
	private final Localizator localizator;
	private final FileUploadHandler fileUploadHandler;
	private final LogoutListener logoutListener;

	private ProgressListener uploadListener = null;
	private FileUploadMonitor uploadMonitor;
	private final DirectoryHandler directoryHandler;

	public MainViewPresenter(WindowManager windowManager, MainViewModel model,
			MainView view, FileActionProvider fileActionProvider,
			FileActionHandler fileActionHandler,
			FileUploadHandler fileUploadHandler,
			DirectoryHandler directoryHandler, Localizator localizator,
			LogoutListener logoutListener) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.fileActionProvider = fileActionProvider;

		this.fileUploadHandler = fileUploadHandler;
		this.directoryHandler = directoryHandler;
		this.localizator = localizator;
		this.logoutListener = logoutListener;
		this.fileUploadHandler.addListener(this);

		fileActionHandler.addRenameListener(createReloadListener());
		fileActionHandler.addDeleteListener(createReloadListener());
	}

	public void initialize() {
		model.refreshRootDirectories(createListener(new Callback() {
			public void onCallback() {
				changeToRootDirectory(model.getRootDirectories().get(0));
			}
		}));
	}

	public void onFileSystemItemSelected(FileSystemItem item, Column column) {
		if (column.equals(Column.NAME)) {
			if (item.isFile()) {
				view.showFileContext((File) item);
			} else {
				Directory directory = (Directory) item;
				if (directory == Directory.Parent)
					moveToParentDirectory();
				else
					changeToDirectory(directory);
			}
		}
	}

	public void changeToRootDirectory(Directory root) {
		model.changeToRootDirectory(root, createRefreshListener());
	}

	public void changeToDirectory(Directory directory) {
		model.changeToSubdirectory(directory, createRefreshListener());
	}

	public void reset() {
		view.clear();
	}

	public void reload() {
		model.refreshData(createListener(new Callback() {
			public void onCallback() {
				refreshView();
			}
		}));
	}

	private void refreshView() {
		List<FileSystemItem> allFileItems = model.getAllItems();
		if (model.getDirectoryModel().canAscend()) {
			allFileItems.add(0, Directory.Parent);
		}
		view.getList().setContent(allFileItems);
		view.refresh();
	}

	public void moveToParentDirectory() {
		if (!model.getDirectoryModel().canAscend())
			return;
		model.moveToParentDirectory(createRefreshListener());
	}

	public void changeToDirectory(int level, Directory directory) {
		model.changeToDirectory(level, directory, createRefreshListener());
	}

	public void onError(MollifyError error) {
		windowManager.getDialogManager().showError(error);
		reload();
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

		if (!model.getSessionInfo().getSettings().isFileUploadProgressEnabled()) {
			return;
		}

		uploadMonitor = new FileUploadMonitor(uploadId,
				new FileUploadProgressListener() {
					public void onProgressUpdate(FileUploadStatus status) {
						int percentage = (int) status.getUploadedPercentage();
						uploadListener.setProgressBarVisible(true);
						uploadListener.setProgress(percentage);
						uploadListener.setDetails(String.valueOf(percentage)
								+ "%");
					}

					public void onProgressUpdateFail(MollifyError error) {
						uploadListener.setProgress(0);
						uploadMonitor.stop();
					}
				}, fileUploadHandler);
		uploadMonitor.start();

	}

	public void onUploadFinished() {
		stopUploaders();
		reload();
	}

	public void onUploadFailed(MollifyError error) {
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

	public void openNewDirectoryDialog() {
		if (model.getCurrentFolder().isEmpty())
			return;
		windowManager.getDialogManager().openCreateFolderDialog(
				model.getCurrentFolder(), directoryHandler,
				createReloadListener());
	}

	private ResultListener createReloadListener() {
		return createListener(new Callback() {
			public void onCallback() {
				reload();
			}
		});
	}

	private ResultListener createRefreshListener() {
		return createListener(new Callback() {
			public void onCallback() {
				refreshView();
			}
		});
	}

	private ResultListener createListener(final Callback callback) {
		return new ResultListener() {
			public void onFail(MollifyError error) {
				onError(error);
			}

			public void onSuccess(Object... result) {
				callback.onCallback();
			}
		};
	}

	public void logout() {
		logoutListener.onLogout(model.getSessionInfo());
	}

}
