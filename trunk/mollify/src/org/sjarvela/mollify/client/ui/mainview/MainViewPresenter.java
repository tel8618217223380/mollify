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
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.filesystem.handler.DirectoryHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.filesystem.upload.DefaultFileUploadListener;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.Callback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.LogoutHandler;
import org.sjarvela.mollify.client.session.PasswordHandler;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.Sort;
import org.sjarvela.mollify.client.ui.dialog.SelectFolderListener;
import org.sjarvela.mollify.client.ui.directoryselector.DirectoryListener;
import org.sjarvela.mollify.client.ui.filelist.DefaultFileItemComparator;
import org.sjarvela.mollify.client.ui.filelist.FileList;

public class MainViewPresenter implements DirectoryListener,
		FileSystemActionHandler, DirectoryHandler, RenameHandler,
		PasswordHandler {
	private final MainViewModel model;
	private final MainView view;
	private final WindowManager windowManager;

	private final FileSystemService fileSystemService;
	private final FileUploadService fileUploadService;
	private final LogoutHandler logoutListener;
	private final TextProvider textProvider;
	private final DirectoryProvider directoryProvider;
	private final SessionService sessionService;
	private final SettingsService settingsService;

	public MainViewPresenter(WindowManager windowManager, MainViewModel model,
			MainView view, SessionService sessionService,
			FileSystemService fileSystemService,
			SettingsService settingsService,
			FileUploadService fileUploadHandler,
			DirectoryProvider directoryProvider, TextProvider textProvider,
			LogoutHandler logoutListener) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.sessionService = sessionService;
		this.fileSystemService = fileSystemService;
		this.settingsService = settingsService;
		this.fileUploadService = fileUploadHandler;
		this.directoryProvider = directoryProvider;
		this.textProvider = textProvider;
		this.logoutListener = logoutListener;

		this.view.setFileContextHandler(this);
		this.view.setDirectoryContextHandler(this);
		this.view.getDirectorySelector().addListener(this);

		this.setListOrder(FileList.COLUMN_NAME, Sort.asc);

		if (model.getSession().isAuthenticationRequired())
			view.getUsername().setText(model.getSession().getLoggedUser());
	}

	public void initialize() {
		if (model.getRootDirectories().size() == 0)
			changeToRootDirectory(null);
		else
			changeToRootDirectory(model.getRootDirectories().get(0));
	}

	public void onFileSystemItemSelected(FileSystemItem item, GridColumn column) {
		if (column.equals(FileList.COLUMN_NAME)) {
			if (item.isFile()) {
				view.showFileContext((File) item);
			} else {
				Directory directory = (Directory) item;

				if (directory == Directory.Parent)
					onMoveToParentDirectory();
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
		model.refreshData(new ResultListener<DirectoryContent>() {
			public void onFail(ServiceError error) {
				onError(error, false);
			}

			public void onSuccess(DirectoryContent result) {
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

	public void onMoveToParentDirectory() {
		if (!model.getDirectoryModel().canAscend())
			return;
		model.moveToParentDirectory(createRefreshListener());
	}

	public void onChangeToDirectory(int level, Directory directory) {
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
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
			return;

		FileUploadListener fileUploadListener = new DefaultFileUploadListener(
				fileUploadService, model.getSession().getSettings()
						.isFileUploadProgressEnabled(), windowManager
						.getDialogManager(), textProvider,
				createReloadListener());

		windowManager.getDialogManager().openUploadDialog(
				model.getCurrentFolder(), fileUploadService,
				model.getSession().getFileSystemInfo(), fileUploadListener);
	}

	public void openNewDirectoryDialog() {
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
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
		return new ResultListener<Object>() {
			public void onFail(ServiceError error) {
				onError(error, true);
			}

			public void onSuccess(Object result) {
				callback.onCallback();
			}
		};
	}

	public void logout() {
		logoutListener.onLogout(model.getSession());
	}

	public void changePassword() {
		windowManager.getDialogManager().openPasswordDialog(this);
	}

	public void changePassword(String oldPassword, String newPassword) {
		sessionService.changePassword(oldPassword, newPassword,
				new ResultListener() {
					public void onFail(ServiceError error) {
						if (error.getType().equals(
								ServiceErrorType.AUTHENTICATION_FAILED)) {
							windowManager
									.getDialogManager()
									.showInfo(
											textProvider.getStrings()
													.passwordDialogTitle(),
											textProvider
													.getStrings()
													.passwordDialogOldPasswordIncorrect());
						} else {
							onError(error, false);
						}
					}

					public void onSuccess(Object result) {
						windowManager
								.getDialogManager()
								.showInfo(
										textProvider.getStrings()
												.passwordDialogTitle(),
										textProvider
												.getStrings()
												.passwordDialogPasswordChangedSuccessfully());
					}
				});
	}

	public void resetPassword(User user, String password) {
		sessionService.resetPassword(user, password,
				createListener(new Callback() {
					public void onCallback() {
						windowManager
								.getDialogManager()
								.showInfo(
										textProvider.getStrings()
												.resetPasswordDialogTitle(),
										textProvider
												.getStrings()
												.passwordDialogPasswordChangedSuccessfully());
					}
				}));
	}

	public void onAction(FileSystemItem item, FileSystemAction action) {
		if (item.isFile())
			onFileAction((File) item, action);
		else
			onDirectoryAction((Directory) item, action);
	}

	private void onFileAction(final File file, FileSystemAction action) {
		if (action.equals(FileSystemAction.download)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadUrl(file));
		} else if (action.equals(FileSystemAction.download_as_zip)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadAsZipUrl(file));
		} else if (action.equals(FileSystemAction.rename)) {
			windowManager.getDialogManager().showRenameDialog(file, this);
		} else {

			if (action.equals(FileSystemAction.copy)) {
				windowManager.getDialogManager().showSelectFolderDialog(
						windowManager.getTextProvider().getStrings()
								.copyFileDialogTitle(),
						windowManager.getTextProvider().getMessages()
								.copyFileMessage(file.getName()),
						windowManager.getTextProvider().getStrings()
								.copyFileDialogAction(), directoryProvider,
						new SelectFolderListener() {
							public void onSelect(Directory selected) {
								copyFile(file, selected);
							}

							public boolean isDirectoryAllowed(
									Directory directory) {
								return !model.getCurrentFolder().equals(
										directory);
							}
						}, model.getDirectoryModel().getDirectoryList());
			} else if (action.equals(FileSystemAction.move)) {
				windowManager.getDialogManager().showSelectFolderDialog(
						windowManager.getTextProvider().getStrings()
								.moveFileDialogTitle(),
						windowManager.getTextProvider().getMessages()
								.moveFileMessage(file.getName()),
						windowManager.getTextProvider().getStrings()
								.moveFileDialogAction(), directoryProvider,
						new SelectFolderListener() {
							public void onSelect(Directory selected) {
								moveFile(file, selected);
							}

							public boolean isDirectoryAllowed(
									Directory directory) {
								return !model.getCurrentFolder().equals(
										directory);
							}
						}, model.getDirectoryModel().getDirectoryList());
			} else if (action.equals(FileSystemAction.delete)) {
				String title = windowManager.getTextProvider().getStrings()
						.deleteFileConfirmationDialogTitle();
				String message = windowManager.getTextProvider().getMessages()
						.confirmFileDeleteMessage(file.getName());
				windowManager.getDialogManager().showConfirmationDialog(title,
						message,
						StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
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
	}

	private void onDirectoryAction(final Directory directory,
			FileSystemAction action) {
		if (action.equals(FileSystemAction.download_as_zip)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadAsZipUrl(directory));
		} else if (action.equals(FileSystemAction.rename)) {
			windowManager.getDialogManager().showRenameDialog(directory, this);
		} else if (action.equals(FileSystemAction.delete)) {
			String title = windowManager.getTextProvider().getStrings()
					.deleteDirectoryConfirmationDialogTitle();
			String message = windowManager.getTextProvider().getMessages()
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

	protected void copyFile(File file, Directory toDirectory) {
		if (model.getCurrentFolder().equals(toDirectory))
			return;
		fileSystemService.copy(file, toDirectory, createReloadListener());
	}

	protected void moveFile(File file, Directory toDirectory) {
		if (model.getCurrentFolder().equals(toDirectory))
			return;
		fileSystemService.move(file, toDirectory, createReloadListener());
	}

	private void delete(FileSystemItem item) {
		fileSystemService.delete(item, createReloadListener());
	}

	public void setListOrder(GridColumn column, Sort sort) {
		view.getList().setComparator(createComparator(column, sort));
	}

	private GridComparator<FileSystemItem> createComparator(GridColumn column,
			Sort sort) {
		return new DefaultFileItemComparator(column, sort);
	}

	public void configure() {
		windowManager.getDialogManager().openConfigurationDialog(
				settingsService, this);
	}
}
