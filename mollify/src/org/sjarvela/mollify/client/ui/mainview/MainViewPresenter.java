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

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryController;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.handler.DirectoryHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.filesystem.upload.DefaultFileUploadListener;
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.Callback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.LogoutHandler;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.filelist.Column;

public class MainViewPresenter implements DirectoryController,
		FileSystemActionHandler, DirectoryHandler, RenameHandler {
	private final MainViewModel model;
	private final MainView view;
	private final WindowManager windowManager;

	private final FileSystemService fileSystemService;
	private final FileUploadService fileUploadService;
	private final LogoutHandler logoutListener;
	private final DefaultTextProvider localizator;

	public MainViewPresenter(WindowManager windowManager, MainViewModel model,
			MainView view, FileSystemService fileSystemService,
			FileUploadService fileUploadHandler, DefaultTextProvider localizator,
			LogoutHandler logoutListener) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.fileSystemService = fileSystemService;
		this.fileUploadService = fileUploadHandler;
		this.localizator = localizator;
		this.logoutListener = logoutListener;

		this.view.setFileContextHandler(this);
		this.view.setDirectoryContextHandler(this);

		if (model.getSessionInfo().isAuthenticationRequired()) {
			view.getUsername().setText(model.getSessionInfo().getLoggedUser());
		}
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
		model.refreshData(new ResultListener() {
			public void onFail(ServiceError error) {
				onError(error, false);
			}

			public void onSuccess(Object... result) {
				refreshView();
			}
		});
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

	public void onError(ServiceError error, boolean reload) {
		windowManager.getDialogManager().showError(error);

		if (reload)
			reload();
		else
			reset();
	}

	public void openUploadDialog() {
		if (model.getCurrentFolder().isEmpty())
			return;

		DefaultFileUploadListener fileUploadListener = new DefaultFileUploadListener(
				fileUploadService, model.getSessionInfo().getSettings()
						.isFileUploadProgressEnabled(), windowManager
						.getDialogManager(), localizator,
				createReloadListener());

		windowManager.getDialogManager().openUploadDialog(
				model.getCurrentFolder(), fileUploadService,
				model.getSessionInfo().getFileSystemInfo(), fileUploadListener);
	}

	public void openNewDirectoryDialog() {
		if (model.getCurrentFolder().isEmpty())
			return;

		windowManager.getDialogManager().openCreateFolderDialog(
				model.getCurrentFolder(), this);
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
			public void onFail(ServiceError error) {
				onError(error, true);
			}

			public void onSuccess(Object... result) {
				callback.onCallback();
			}
		};
	}

	public void logout() {
		logoutListener.onLogout(model.getSessionInfo());
	}

	public void onAction(FileSystemItem item, FileSystemAction action) {
		if (item.isFile())
			onFileAction((File) item, action);
		else
			onDirectoryAction((Directory) item, action);
	}

	private void onFileAction(final File file, FileSystemAction action) {
		if (action.equals(FileSystemAction.download)
				|| action.equals(FileSystemAction.download_as_zip)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadUrl(file));
		} else if (action.equals(FileSystemAction.rename)) {
			windowManager.getDialogManager().showRenameDialog(file, this);
		} else if (action.equals(FileSystemAction.delete)) {
			String title = windowManager.getLocalizator().getStrings()
					.deleteFileConfirmationDialogTitle();
			String message = windowManager.getLocalizator().getMessages()
					.confirmFileDeleteMessage(file.getName());
			windowManager.getDialogManager().showConfirmationDialog(title,
					message, StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
					new ConfirmationListener() {
						public void onConfirm() {
							delete(file);
						}
					});
		} else {
			windowManager.getDialogManager().showInfo("ERROR",
					"Unsupported action:" + action.name());
		}
	}

	private void onDirectoryAction(final Directory directory,
			FileSystemAction action) {
		if (action.equals(FileSystemAction.delete)) {
			String title = windowManager.getLocalizator().getStrings()
					.deleteDirectoryConfirmationDialogTitle();
			String message = windowManager.getLocalizator().getMessages()
					.confirmDirectoryDeleteMessage(directory.getName());
			windowManager.getDialogManager().showConfirmationDialog(title,
					message, StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
					new ConfirmationListener() {
						public void onConfirm() {
							delete(directory);
						}
					});
		} else {
			windowManager.getDialogManager().showInfo("ERROR",
					"Unsupported action:" + action.name());
		}
	}

	public void createDirectory(Directory parentFolder, String folderName) {
		fileSystemService.createDirectory(parentFolder, folderName,
				createReloadListener());
	}

	public void rename(FileSystemItem item, String newName) {
		fileSystemService.rename(item, newName, createReloadListener());
	}

	private void delete(FileSystemItem item) {
		fileSystemService.delete(item, createReloadListener());
	}
}
