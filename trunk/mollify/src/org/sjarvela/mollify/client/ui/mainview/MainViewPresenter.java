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
import org.sjarvela.mollify.client.file.DirectoryProvider;
import org.sjarvela.mollify.client.file.FileAction;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileDetailsProvider;
import org.sjarvela.mollify.client.file.FileOperationHandler;
import org.sjarvela.mollify.client.service.FileUploadResultHandler;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.WindowManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class MainViewPresenter implements DirectoryController,
		DirectoryProvider, FileDetailsProvider, FileActionHandler,
		FileOperationHandler {
	private final MollifyService service;
	private final MainViewModel model;
	private final MainView view;
	private final WindowManager windowManager;
	private final FileActionProvider fileActionProvider;

	public MainViewPresenter(MollifyService service,
			WindowManager windowManager, MainViewModel model, MainView view,
			FileActionProvider fileActionProvider) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.service = service;
		this.fileActionProvider = fileActionProvider;

		view.initialize(this, this, this, this);
	}

	public void initialize() {
		getRootDirectories();
	}

	public void getRootDirectories() {
		this.service
				.getRootDirectories(createDefaultListener(new ResultCallback() {
					public void onCallback(JavaScriptObject result) {
						JsArray<Directory> roots = result.cast();
						updateRootDirs(roots);
					}
				}));
	}

	private void updateRootDirs(JsArray<Directory> roots) {
		model.setRootDirectories(roots);

		// select first one if none was selected
		if (roots.length() > 0
				&& model.getDirectoryModel().getRootDirectory().isEmpty()) {
			model.getDirectoryModel().setRootDirectory(roots.get(0));
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

		this.service.getDirectories(createDefaultListener(new ResultCallback() {
			public void onCallback(JavaScriptObject result) {
				final JsArray<Directory> directories = result.cast();

				service.getFiles(createDefaultListener(new ResultCallback() {
					public void onCallback(JavaScriptObject result) {
						JsArray<File> files = result.cast();
						refreshList(directories, files);
					}
				}), folder);
			}
		}), folder);
	}

	private void refreshList(JsArray<Directory> directories, JsArray<File> files) {
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

	public void getFileDetails(File file, ResultListener resultListener) {
		service.getFileDetails(file, resultListener);
	}

	public void openUploadDialog() {
		if (model.getDirectoryModel().getCurrentFolder().isEmpty())
			return;

		windowManager.getDialogManager().openUploadDialog(
				model.getDirectoryModel().getCurrentFolder(),
				fileActionProvider, this);
	}

	public void onFileAction(final File file, FileAction action) {
		if (action.equals(FileAction.DOWNLOAD)) {
			windowManager.openDownloadUrl(fileActionProvider.getActionURL(file,
					action));
		} else if (action.equals(FileAction.RENAME)) {
			windowManager.getDialogManager().showRenameDialog(file, this);
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
							onDelete(file);
						}
					});
		} else {
			windowManager.getDialogManager().showInfo("ERROR",
					"Unsupported action:" + action.name());
		}

	}

	public void onRename(File file, String newName) {
		service.renameFile(file, newName,
				createDefaultListener(new ResultCallback() {
					public void onCallback(JavaScriptObject result) {
						refresh();
					}
				}));
	}

	public void onDelete(File file) {
		service.deleteFile(file, createDefaultListener(new ResultCallback() {
			public void onCallback(JavaScriptObject result) {
				refresh();
			}
		}));
	}

	public FileUploadResultHandler getFileUploadResultHandler() {
		return new FileUploadResultHandler(
				createDefaultListener(new ResultCallback() {
					public void onCallback(JavaScriptObject result) {
						refresh();
					}
				}));
	}

	public String getNewUploadId() {
		return service.getNewUploadId();
	}

	public void getUploadProgress(String id, ResultCallback callback) {
		service.getUploadProgress(id, createDefaultListener(callback));
	}

	private ResultListener createDefaultListener(final ResultCallback callback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				windowManager.getDialogManager().showError(error);
				reset();
			}

			public void onSuccess(JavaScriptObject result) {
				callback.onCallback(result);
			}
		};
	}
}
