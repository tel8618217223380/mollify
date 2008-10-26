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

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.file.DirectoryController;
import org.sjarvela.mollify.client.file.FileAction;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.WindowManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class MainViewPresenter implements DirectoryController,
		FileActionHandler, ViewListener, FileUploadListener {
	private final FileViewModel model;
	private final MainView view;

	private final WindowManager windowManager;
	private final FileActionProvider fileActionProvider;
	private final FileOperator fileOperator;
	private final FileUploadHandler uploadHandler;

	public MainViewPresenter(MollifyService service,
			WindowManager windowManager, FileViewModel model, MainView view,
			FileActionProvider fileActionProvider, FileOperator fileOperator,
			FileUploadHandler uploadHandler) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.fileActionProvider = fileActionProvider;
		this.fileOperator = fileOperator;
		this.uploadHandler = uploadHandler;
		this.uploadHandler.addListener(this);

		view.addViewListener(this);
		view.getDirectorySelector().initialize(fileOperator, this);
	}

	public void onViewLoad() {
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

	public void openUploadDialog() {
		if (model.getDirectoryModel().getCurrentFolder().isEmpty())
			return;

		windowManager.getDialogManager().openUploadDialog(
				model.getDirectoryModel().getCurrentFolder(),
				fileActionProvider, uploadHandler);
	}

	public void onFileAction(final File file, FileAction action) {
		if (action.equals(FileAction.DOWNLOAD)) {
			windowManager.openDownloadUrl(fileActionProvider.getActionURL(file,
					action));
		} else if (action.equals(FileAction.RENAME)) {
			windowManager.getDialogManager().showRenameDialog(file,
					fileOperator, createRefreshListener());
		} else if (action.equals(FileAction.DELETE)) {
			String title = windowManager.getLocalizator().getStrings()
					.deleteFileConfirmationDialogTitle();
			String message = windowManager.getLocalizator().getMessages()
					.confirmFileDeleteMessage(file.getName());
			windowManager.getDialogManager().showConfirmationDialog(title,
					message,
					StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE_FILE,
					new ConfirmationListener() {
						public void onConfirm() {
							fileOperator
									.onDelete(file, createRefreshListener());
						}
					});
		} else {
			windowManager.getDialogManager().showInfo("ERROR",
					"Unsupported action:" + action.name());
		}

	}

	public void onUploadStarted() {
		//windowManager.getDialogManager().openProgressDialog();
	}
	
	public void onUploadFinished() {
		refresh();
	}

	public void onUploadFailed(ServiceError error) {
		windowManager.getDialogManager().showError(error);
		reset();
	}

	private ResultListener createRefreshListener() {
		return createDefaultListener(new ResultCallback() {
			public void onCallback(JavaScriptObject... result) {
				refresh();
			}
		});
	}

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
