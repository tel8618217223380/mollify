/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderContent;
import org.sjarvela.mollify.client.filesystem.handler.DirectoryHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemPermissionHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.Callback;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.Sort;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.filelist.DefaultFileItemComparator;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.folderselector.FolderListener;
import org.sjarvela.mollify.client.ui.mainview.CreateFolderDialogFactory;
import org.sjarvela.mollify.client.ui.password.PasswordDialogFactory;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;
import org.sjarvela.mollify.client.util.Html;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class MainViewPresenter implements FolderListener, PasswordHandler,
		FileItemDescriptionHandler, FileSystemPermissionHandler {
	private final MainViewModel model;
	private final DefaultMainView view;
	private final DialogManager dialogManager;
	private final SessionManager sessionManager;

	private final FileSystemService fileSystemService;
	private final ConfigurationService configurationService;
	private final FileSystemActionHandler fileSystemActionHandler;
	private final TextProvider textProvider;
	private final PermissionEditorViewFactory permissionEditorViewFactory;
	private final PasswordDialogFactory passwordDialogFactory;
	private final FileUploadDialogFactory fileUploadDialogFactory;
	private final CreateFolderDialogFactory createFolderDialogFactory;
	private final ViewManager viewManager;

	public MainViewPresenter(DialogManager dialogManager,
			ViewManager viewManager, SessionManager sessionManager,
			MainViewModel model, DefaultMainView view,
			ConfigurationService configurationService,
			FileSystemService fileSystemService, TextProvider textProvider,
			FileSystemActionHandlerFactory fileSystemActionHandlerFactory,
			PermissionEditorViewFactory permissionEditorViewFactory,
			PasswordDialogFactory passwordDialogFactory,
			FileUploadDialogFactory fileUploadDialogFactory,
			CreateFolderDialogFactory createFolderDialogFactory) {
		this.dialogManager = dialogManager;
		this.viewManager = viewManager;
		this.sessionManager = sessionManager;
		this.configurationService = configurationService;
		this.fileSystemService = fileSystemService;

		this.model = model;
		this.view = view;
		this.textProvider = textProvider;
		this.permissionEditorViewFactory = permissionEditorViewFactory;
		this.passwordDialogFactory = passwordDialogFactory;
		this.fileUploadDialogFactory = fileUploadDialogFactory;
		this.createFolderDialogFactory = createFolderDialogFactory;
		this.fileSystemActionHandler = fileSystemActionHandlerFactory
				.create(createReloadCallback());

		this.view.getFileContext()
				.setFileActionHandler(fileSystemActionHandler);
		this.view.getFileContext().setFileItemDescriptionHandler(this);
		this.view.getFileContext().setFilePermissionHandler(this);

		this.view.getDirectoryContext().setFolderActionHandler(
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
				Folder directory = (Folder) item;

				if (directory == Folder.Parent)
					onMoveToParentFolder();
				else
					changeToDirectory(directory);
			}
		}
	}

	public void changeToRootDirectory(Folder root) {
		model.changeToRootDirectory(root, createRefreshListener());
	}

	public void changeToDirectory(Folder directory) {
		model.changeToSubdirectory(directory, createRefreshListener());
	}

	public void reset() {
		view.clear();
	}

	public void reload() {
		model.refreshData(new ResultListener<FolderContent>() {
			public void onFail(ServiceError error) {
				onError(error, false);
			}

			public void onSuccess(FolderContent result) {
				refreshView();
			}
		});
	}

	private void refreshView() {
		List<FileSystemItem> allFileItems = model.getAllItems();
		if (model.getFolderModel().canAscend())
			allFileItems.add(0, Folder.Parent);

		view.getList().setContent(allFileItems);
		view.refresh();
	}

	public void onMoveToParentFolder() {
		if (!model.getFolderModel().canAscend())
			return;
		model.moveToParentDirectory(createRefreshListener());
	}

	public void onChangeToFolder(int level, Folder directory) {
		model.changeToDirectory(level, directory, createRefreshListener());
	}

	public void onError(ServiceError error, boolean reload) {
		dialogManager.showError(error);

		if (reload)
			reload();
		else
			reset();
	}

	public void openUploadDialog() {
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
			return;

		fileUploadDialogFactory.openFileUploadDialog(model.getCurrentFolder(),
				createReloadListener("Upload"));
	}

	public void openNewDirectoryDialog() {
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
			return;

		createFolderDialogFactory.openCreateFolderDialog(model
				.getCurrentFolder(), new DirectoryHandler() {
			public void createDirectory(Folder parentFolder, String folderName) {
				fileSystemService.createFolder(parentFolder, folderName,
						createReloadListener("Create folder"));
			}
		});
	}

	private ResultListener createReloadListener(final String operation) {
		return createListener(new Callback() {
			public void onCallback() {
				DeferredCommand.addCommand(new Command() {
					@Override
					public void execute() {
						Log.debug(operation + " complete");
						reload();
					}
				});
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
		sessionManager.endSession();
	}

	public void changePassword() {
		passwordDialogFactory.openPasswordDialog(this);
	}

	public void changePassword(String oldPassword, String newPassword) {
		configurationService.changePassword(oldPassword, newPassword,
				new ResultListener() {
					public void onFail(ServiceError error) {
						if (ServiceErrorType.AUTHENTICATION_FAILED.equals(error
								.getType())) {
							dialogManager.showInfo(textProvider.getStrings()
									.passwordDialogTitle(), textProvider
									.getStrings()
									.passwordDialogOldPasswordIncorrect());
						} else {
							onError(error, false);
						}
					}

					public void onSuccess(Object result) {
						dialogManager.showInfo(textProvider.getStrings()
								.passwordDialogTitle(), textProvider
								.getStrings()
								.passwordDialogPasswordChangedSuccessfully());
					}
				});
	}

	public void setListOrder(GridColumn column, Sort sort) {
		view.getList().setComparator(createComparator(column, sort));
	}

	private GridComparator<FileSystemItem> createComparator(GridColumn column,
			Sort sort) {
		return new DefaultFileItemComparator(column, sort);
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
			dialogManager.showInfo(textProvider.getStrings()
					.infoDialogErrorTitle(), textProvider.getStrings()
					.invalidDescriptionUnsafeTags());
			return false;
		}
		return true;
	}

	public void onEditPermissions(FileSystemItem item) {
		permissionEditorViewFactory.openPermissionEditor(item);
	}

	public void onEditItemPermissions() {
		permissionEditorViewFactory.openPermissionEditor(null);
	}

	public void onOpenAdministration() {
		viewManager.openUrlInNewWindow(configurationService
				.getAdministrationUrl());
	}

	public void onToggleSelectMode() {
		view.setSelectMode(view.selectModeButton().isDown());
	}

	public void onFileSystemItemSelectionChanged(List<FileSystemItem> selected) {
		view.updateFileSelection(selected);
	}
}
