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
import org.sjarvela.mollify.client.DirectoryController;
import org.sjarvela.mollify.client.DirectoryProvider;
import org.sjarvela.mollify.client.FileAction;
import org.sjarvela.mollify.client.FileActionHandler;
import org.sjarvela.mollify.client.FileActionProvider;
import org.sjarvela.mollify.client.FileDetailsProvider;
import org.sjarvela.mollify.client.FileOperationHandler;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.FileUploadResultHandler;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
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
		this.service.checkAuthentication(new ResultListener() {
			public void onFail(ServiceError error) {
				if (ServiceError.AUTHENTICATION_FAILED.equals(error)) {
					showLogin();
					return;
				}
				windowManager.showError(error);
				reset();
			}

			public void onSuccess(JavaScriptObject result) {
				getRootDirectories();
			}
		});
	}

	private void showLogin() {
		windowManager.showInfo("Login", "Login here");
	}

	public void getRootDirectories() {
		this.service
				.getRootDirectories(createDefaultListener(new ResultCallback() {
					public void onCallback(JavaScriptObject result) {
						JsArray<Directory> dirs = result.cast();
						model.setRootDirectories(dirs);

						// select first one if none was selected
						if (dirs.length() > 0
								&& model.getDirectoryModel().getRootDirectory()
										.isEmpty()) {
							model.getDirectoryModel().setRootDirectory(
									dirs.get(0));
							refresh();
						}
					}
				}));
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

	// private void onOperationFailed(ErrorValue result) {
	// GWT.log(result.getError(), null);
	// view.showError(result);
	// }

	public void getFileDetails(File file, ResultListener resultListener) {
		service.getFileDetails(file, resultListener);
	}

	public void openUploadDialog() {
		windowManager.openUploadDialog(model.getDirectoryModel()
				.getCurrentFolder(), fileActionProvider, this);
	}

	public void onFileAction(final File file, FileAction action) {
		if (action.equals(FileAction.DOWNLOAD)) {
			windowManager.openDownloadUrl(fileActionProvider.getActionURL(file,
					action));
		} else if (action.equals(FileAction.RENAME)) {
			windowManager.showRenameDialog(file, this);
		} else if (action.equals(FileAction.DELETE)) {
			windowManager.showFileDeleteConfirmationDialog(file,
					new ConfirmationListener() {
						public void onConfirm() {
							onDelete(file);
						}
					});
		} else {
			windowManager.showInfo("ERROR", "Unsupported action:"
					+ action.name());
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

	private ResultListener createDefaultListener(final ResultCallback callback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				windowManager.showError(error);
				reset();
			}

			public void onSuccess(JavaScriptObject result) {
				callback.onCallback(result);
			}
		};
	}
}
