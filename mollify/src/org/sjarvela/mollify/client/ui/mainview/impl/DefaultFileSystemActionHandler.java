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
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.Callback;
import org.sjarvela.mollify.client.service.ConfirmationListener;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.itemselector.ItemSelectorFactory;
import org.sjarvela.mollify.client.ui.itemselector.SelectItemHandler;
import org.sjarvela.mollify.client.ui.mainview.RenameDialogFactory;

import com.google.gwt.user.client.ui.Widget;

public class DefaultFileSystemActionHandler implements FileSystemActionHandler,
		RenameHandler {
	private final ViewManager windowManager;
	private final DialogManager dialogManager;
	private final FileSystemService fileSystemService;
	private final Callback actionCallback;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final TextProvider textProvider;
	private final ItemSelectorFactory itemSelectorFactory;
	private final RenameDialogFactory renameDialogFactory;

	public DefaultFileSystemActionHandler(TextProvider textProvider,
			ViewManager windowManager, DialogManager dialogManager,
			ItemSelectorFactory itemSelectorFactory,
			RenameDialogFactory renameDialogFactory,
			FileSystemService fileSystemService,
			FileSystemItemProvider fileSystemItemProvider,
			Callback actionCallback) {
		this.textProvider = textProvider;
		this.windowManager = windowManager;
		this.dialogManager = dialogManager;
		this.itemSelectorFactory = itemSelectorFactory;
		this.renameDialogFactory = renameDialogFactory;
		this.fileSystemService = fileSystemService;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.actionCallback = actionCallback;
	}

	public void onAction(FileSystemItem item, FileSystemAction action,
			Widget source) {
		if (item.isFile())
			onFileAction((File) item, action, source);
		else
			onFolderAction((Folder) item, action, source);
	}

	private void onFileAction(final File file, FileSystemAction action,
			Widget source) {
		if (action.equals(FileSystemAction.download)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadUrl(file));
		} else if (action.equals(FileSystemAction.download_as_zip)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadAsZipUrl(file));
		} else if (action.equals(FileSystemAction.rename)) {
			renameDialogFactory.openRenameDialog(file, this, source);
		} else {
			if (action.equals(FileSystemAction.copy)) {
				itemSelectorFactory.openFolderSelector(textProvider
						.getStrings().copyFileDialogTitle(), textProvider
						.getMessages().copyFileMessage(file.getName()),
						textProvider.getStrings().copyFileDialogAction(),
						fileSystemItemProvider, new SelectItemHandler() {
							public void onSelect(FileSystemItem selected) {
								copyFile(file, (Folder) selected);
							}

							public boolean isItemAllowed(FileSystemItem item,
									List<Folder> path) {
								if (item.isFile())
									return false;
								return !item.getId().equals(file.getParentId());
							}
						});
			} else if (action.equals(FileSystemAction.move)) {
				itemSelectorFactory.openFolderSelector(textProvider
						.getStrings().moveFileDialogTitle(), textProvider
						.getMessages().moveFileMessage(file.getName()),
						textProvider.getStrings().moveFileDialogAction(),
						fileSystemItemProvider, new SelectItemHandler() {
							public void onSelect(FileSystemItem selected) {
								moveFile(file, (Folder) selected);
							}

							public boolean isItemAllowed(FileSystemItem item,
									List<Folder> path) {
								if (item.isFile())
									return false;
								return !item.getId().equals(file.getParentId());
							}
						});
			} else if (action.equals(FileSystemAction.delete)) {
				String title = textProvider.getStrings()
						.deleteFileConfirmationDialogTitle();
				String message = textProvider.getMessages()
						.confirmFileDeleteMessage(file.getName());
				dialogManager.showConfirmationDialog(title, message,
						StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
						new ConfirmationListener() {
							public void onConfirm() {
								delete(file);
							}
						}, source);
			} else {
				dialogManager.showInfo("ERROR", "Unsupported action:"
						+ action.name());
			}
		}
	}

	private void onFolderAction(final Folder folder, FileSystemAction action,
			Widget source) {
		if (action.equals(FileSystemAction.download_as_zip)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadAsZipUrl(folder));
		} else if (action.equals(FileSystemAction.rename)) {
			renameDialogFactory.openRenameDialog(folder, this, source);
		} else if (action.equals(FileSystemAction.move)) {
			itemSelectorFactory.openFolderSelector(textProvider.getStrings()
					.moveDirectoryDialogTitle(), textProvider.getMessages()
					.moveDirectoryMessage(folder.getName()), textProvider
					.getStrings().moveDirectoryDialogAction(),
					fileSystemItemProvider, new SelectItemHandler() {
						public void onSelect(FileSystemItem selected) {
							moveFolder(folder, (Folder) selected);
						}

						public boolean isItemAllowed(FileSystemItem candidate,
								List<Folder> path) {
							if (candidate.isFile())
								return false;

							return !folder.equals(candidate)
									&& !folder.equals(candidate)
									&& !path.contains(folder);
						}
					});
		} else if (action.equals(FileSystemAction.delete)) {
			String title = textProvider.getStrings()
					.deleteDirectoryConfirmationDialogTitle();
			String message = textProvider.getMessages()
					.confirmDirectoryDeleteMessage(folder.getName());
			dialogManager.showConfirmationDialog(title, message,
					StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
					new ConfirmationListener() {
						public void onConfirm() {
							delete(folder);
						}
					}, source);
		} else {
			dialogManager.showInfo("ERROR", "Unsupported action:"
					+ action.name());
		}
	}

	public void rename(FileSystemItem item, String newName) {
		fileSystemService.rename(item, newName, createListener());
	}

	protected void copyFile(File file, Folder toDirectory) {
		if (toDirectory.getId().equals(file.getParentId()))
			return;
		fileSystemService.copy(file, toDirectory, createListener());
	}

	protected void moveFile(File file, Folder toDirectory) {
		if (toDirectory.getId().equals(file.getParentId()))
			return;
		fileSystemService.move(file, toDirectory, createListener());
	}

	protected void moveFolder(Folder directory, Folder toDirectory) {
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
				dialogManager.showError(error);
			}

			public void onSuccess(Object result) {
				actionCallback.onCallback();
			}
		};
	}
}
