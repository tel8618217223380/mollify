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
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.directorymodel.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.DirectoryHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemPermissionHandler;
import org.sjarvela.mollify.client.filesystem.upload.DefaultFileUploadListener;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.LogoutHandler;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.Sort;
import org.sjarvela.mollify.client.ui.directoryselector.DirectoryListener;
import org.sjarvela.mollify.client.ui.filelist.DefaultFileItemComparator;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.util.Html;

public class MainViewPresenter implements DirectoryListener, PasswordHandler,
		FileItemDescriptionHandler, FileSystemPermissionHandler {
	private final MainViewModel model;
	private final MainView view;
	private final WindowManager windowManager;

	private final FileSystemService fileSystemService;
	private final FileUploadService fileUploadService;
	private final SessionService sessionService;
	private final ConfigurationService configurationService;
	private final FileSystemActionHandler fileSystemActionHandler;
	private final LogoutHandler logoutHandler;
	private final TextProvider textProvider;
	private final FileSystemItemProvider fileSystemItemProvider;

	public MainViewPresenter(WindowManager windowManager, MainViewModel model,
			MainView view, SessionService sessionService,
			FileSystemService fileSystemService,
			ConfigurationService configurationService,
			FileUploadService fileUploadService, TextProvider textProvider,
			FileSystemItemProvider fileSystemItemProvider,
			LogoutHandler logoutHandler,
			FileSystemActionHandlerFactory fileSystemActionHandlerFactory) {
		this.sessionService = sessionService;
		this.fileSystemService = fileSystemService;
		this.configurationService = configurationService;
		this.fileUploadService = fileUploadService;

		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.textProvider = textProvider;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.logoutHandler = logoutHandler;
		this.fileSystemActionHandler = fileSystemActionHandlerFactory
				.create(createReloadCallback());

		this.view.getFileContext()
				.setFileActionHandler(fileSystemActionHandler);
		this.view.getFileContext().setFileItemDescriptionHandler(this);
		this.view.getFileContext().setFilePermissionHandler(this);

		this.view.getDirectoryContext().setDirectoryActionHandler(
				fileSystemActionHandler);
		this.view.getDirectoryContext().setFileItemDescriptionHandler(this);
		this.view.getDirectoryContext().setFilePermissionHandler(this);
		this.view.getDirectorySelector().addListener(this);

		this.setListOrder(FileList.COLUMN_NAME, Sort.asc);

		if (model.getSession().isAuthenticationRequired())
			view.getUsername().setText(model.getSession().getLoggedUser());
	}

	public void initialize() {
		if (model.getRootDirectories().size() == 0) {
			changeToRootDirectory(null);
			view.hideButtons();
		} else {
			changeToRootDirectory(model.getRootDirectories().get(0));
		}
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
		if (model.getDirectoryModel().canAscend())
			allFileItems.add(0, Directory.Parent);

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
				fileUploadService, model.getSession().getFeatures()
						.fileUploadProgress(),
				windowManager.getDialogManager(), textProvider,
				createReloadListener());

		windowManager.getDialogManager().openUploadDialog(
				model.getCurrentFolder(), fileUploadService,
				model.getSession().getFileSystemInfo(), fileUploadListener);
	}

	public void openNewDirectoryDialog() {
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
			return;

		windowManager.getDialogManager().openCreateFolderDialog(
				model.getCurrentFolder(), new DirectoryHandler() {
					public void createDirectory(Directory parentFolder,
							String folderName) {
						fileSystemService.createDirectory(parentFolder,
								folderName, createReloadListener());
					}
				});
	}

	private ResultListener createReloadListener() {
		return createListener(new Callback() {
			public void onCallback() {
				reload();
			}
		});
	}

	private ResultListener createRefreshListener() {
		return createListener(createRefreshCallback());
	}

	private Callback createReloadCallback() {
		return new Callback() {
			public void onCallback() {
				reload();
			}
		};
	}

	private Callback createRefreshCallback() {
		return new Callback() {
			public void onCallback() {
				refreshView();
			}
		};
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
		logoutHandler.onLogout(model.getSession());
	}

	public void changePassword() {
		windowManager.getDialogManager().openPasswordDialog(this);
	}

	public void changePassword(String oldPassword, String newPassword) {
		sessionService.changePassword(oldPassword, newPassword,
				new ResultListener() {
					public void onFail(ServiceError error) {
						if (ServiceErrorType.AUTHENTICATION_FAILED.equals(error
								.getType())) {
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

	public void setListOrder(GridColumn column, Sort sort) {
		view.getList().setComparator(createComparator(column, sort));
	}

	private GridComparator<FileSystemItem> createComparator(GridColumn column,
			Sort sort) {
		return new DefaultFileItemComparator(column, sort);
	}

	public void configure() {
		windowManager.getDialogManager().openConfigurationDialog(
				configurationService, this);
	}

	public void setItemDescription(FileSystemItem item, String description,
			Callback successCallback) {
		fileSystemService.setItemDescription(item, description,
				createListener(successCallback));
	}

	public void removeItemDescription(FileSystemItem item,
			Callback successCallback) {
		fileSystemService.removeItemDescription(item,
				createListener(successCallback));
	}

	public boolean validateDescription(String description) {
		List<String> unsafeTags = Html.findUnsafeHtmlTags(description);
		if (unsafeTags.size() > 0) {
			windowManager.getDialogManager().showInfo(
					textProvider.getStrings().infoDialogErrorTitle(),
					textProvider.getStrings().invalidDescriptionUnsafeTags());
			return false;
		}
		return true;
	}

	public void onEditPermissions(FileSystemItem item) {
		windowManager.getDialogManager().openFilePermissionEditor(
				configurationService, fileSystemService,
				fileSystemItemProvider, item);
	}

	public void onEditItemPermissions() {
		windowManager.getDialogManager().openFilePermissionEditor(
				configurationService, fileSystemService,
				fileSystemItemProvider, null);
	}
}
