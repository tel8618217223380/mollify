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
import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.directorymodel.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.dialog.SelectItemHandler;

public class DefaultFileSystemActionHandler implements FileSystemActionHandler,
		RenameHandler {
	private WindowManager windowManager;
	private FileSystemService fileSystemService;
	private Callback actionCallback;
	private FileSystemItemProvider fileSystemItemProvider;

	public DefaultFileSystemActionHandler(WindowManager windowManager,
			FileSystemService fileSystemService,
			FileSystemItemProvider fileSystemItemProvider, Callback actionCallback) {
		this.windowManager = windowManager;
		this.fileSystemService = fileSystemService;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.actionCallback = actionCallback;
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
								.copyFileDialogAction(), fileSystemItemProvider,
						new SelectItemHandler() {
							public void onSelect(FileSystemItem selected) {
								copyFile(file, (Directory) selected);
							}

							public boolean isItemAllowed(FileSystemItem item,
									List<Directory> path) {
								if (item.isFile())
									return false;
								return !item.getId().equals(file.getParentId());
							}
						});
			} else if (action.equals(FileSystemAction.move)) {
				windowManager.getDialogManager().showSelectFolderDialog(
						windowManager.getTextProvider().getStrings()
								.moveFileDialogTitle(),
						windowManager.getTextProvider().getMessages()
								.moveFileMessage(file.getName()),
						windowManager.getTextProvider().getStrings()
								.moveFileDialogAction(), fileSystemItemProvider,
						new SelectItemHandler() {
							public void onSelect(FileSystemItem selected) {
								moveFile(file, (Directory) selected);
							}

							public boolean isItemAllowed(FileSystemItem item,
									List<Directory> path) {
								if (item.isFile())
									return false;
								return !item.getId().equals(file.getParentId());
							}
						});
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
		} else if (action.equals(FileSystemAction.move)) {
			windowManager.getDialogManager().showSelectFolderDialog(
					windowManager.getTextProvider().getStrings()
							.moveDirectoryDialogTitle(),
					windowManager.getTextProvider().getMessages()
							.moveDirectoryMessage(directory.getName()),
					windowManager.getTextProvider().getStrings()
							.moveDirectoryDialogAction(), fileSystemItemProvider,
					new SelectItemHandler() {
						public void onSelect(FileSystemItem selected) {
							moveDirectory(directory, (Directory) selected);
						}

						public boolean isItemAllowed(FileSystemItem candidate,
								List<Directory> path) {
							if (candidate.isFile())
								return false;

							return !directory.equals(candidate)
									&& !directory.equals(candidate)
									&& !path.contains(directory);
						}
					});
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

	public void rename(FileSystemItem item, String newName) {
		fileSystemService.rename(item, newName, createListener());
	}

	protected void copyFile(File file, Directory toDirectory) {
		if (toDirectory.getId().equals(file.getParentId()))
			return;
		fileSystemService.copy(file, toDirectory, createListener());
	}

	protected void moveFile(File file, Directory toDirectory) {
		if (toDirectory.getId().equals(file.getParentId()))
			return;
		fileSystemService.move(file, toDirectory, createListener());
	}

	protected void moveDirectory(Directory directory, Directory toDirectory) {
		if (directory.equals(toDirectory))
			return;
		fileSystemService.move(directory, toDirectory, createListener());
	}

	private void delete(FileSystemItem item) {
		fileSystemService.delete(item, createListener());
	}

	private ResultListener createListener() {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				windowManager.getDialogManager().showError(error);
			}

			public void onSuccess(Object result) {
				actionCallback.onCallback();
			}
		};
	}
}
