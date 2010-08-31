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

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionListener;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ConfirmationListener;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dialog.InputListener;
import org.sjarvela.mollify.client.ui.itemselector.ItemSelectorFactory;
import org.sjarvela.mollify.client.ui.itemselector.SelectItemHandler;
import org.sjarvela.mollify.client.ui.mainview.RenameDialogFactory;
import org.sjarvela.mollify.client.ui.viewer.FileViewerFactory;

import com.google.gwt.user.client.ui.Widget;

public class DefaultFileSystemActionHandler implements FileSystemActionHandler,
		RenameHandler {
	private final ViewManager windowManager;
	private final DialogManager dialogManager;
	private final FileSystemService fileSystemService;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final TextProvider textProvider;
	private final ItemSelectorFactory itemSelectorFactory;
	private final RenameDialogFactory renameDialogFactory;
	private final FileViewerFactory fileViewerFactory;
	private final SessionInfo session;

	private final List<FileSystemActionListener> listeners = new ArrayList();

	public DefaultFileSystemActionHandler(TextProvider textProvider,
			ViewManager windowManager, DialogManager dialogManager,
			ItemSelectorFactory itemSelectorFactory,
			RenameDialogFactory renameDialogFactory,
			FileViewerFactory fileViewerFactory,
			FileSystemService fileSystemService,
			FileSystemItemProvider fileSystemItemProvider, SessionInfo session) {
		this.textProvider = textProvider;
		this.windowManager = windowManager;
		this.dialogManager = dialogManager;
		this.itemSelectorFactory = itemSelectorFactory;
		this.renameDialogFactory = renameDialogFactory;
		this.fileViewerFactory = fileViewerFactory;
		this.fileSystemService = fileSystemService;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.session = session;
	}

	public void onAction(FileSystemItem item, FileSystemAction action,
			Widget source, Object param) {
		if (item.isFile())
			onFileAction((File) item, action, source, param);
		else
			onFolderAction((Folder) item, action, source);
	}

	public void onAction(final List<FileSystemItem> items,
			final FileSystemAction action, Folder folder, Widget source,
			final Callback cb) {
		if (FileSystemAction.delete.equals(action)) {
			String title = textProvider.getStrings()
					.deleteFileConfirmationDialogTitle();
			String message = textProvider.getMessages()
					.confirmMultipleItemDeleteMessage(items.size());
			dialogManager.showConfirmationDialog(title, message,
					StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
					new ConfirmationListener() {
						public void onConfirm() {
							fileSystemService.delete(items, createListener(
									action, cb));
						}
					}, source);
		} else if (FileSystemAction.copy.equals(action)) {
			if (folder == null) {
				itemSelectorFactory.openFolderSelector(textProvider
						.getStrings().copyMultipleItemsTitle(), textProvider
						.getMessages().copyMultipleItemsMessage(items.size()),
						textProvider.getStrings().copyFileDialogAction(),
						fileSystemItemProvider, new SelectItemHandler() {
							public void onSelect(FileSystemItem selected) {
								fileSystemService.copy(items,
										(Folder) selected, createListener(
												action, cb));
							}

							public boolean isItemAllowed(FileSystemItem item,
									List<Folder> path) {
								if (item.isFile())
									return false;
								return canCopyTo(items, (Folder) item);
							}
						}, source);
				return;
			}

			if (!canCopyTo(items, folder)) {
				dialogManager.showInfo(textProvider.getStrings()
						.copyMultipleItemsTitle(), textProvider.getStrings()
						.cannotCopyAllItemsMessage());
				return;
			}

			fileSystemService.copy(items, folder, createListener(action, cb));
		} else if (FileSystemAction.move.equals(action)) {
			if (folder == null) {
				itemSelectorFactory.openFolderSelector(textProvider
						.getStrings().moveMultipleItemsTitle(), textProvider
						.getMessages().moveMultipleItemsMessage(items.size()),
						textProvider.getStrings().moveFileDialogAction(),
						fileSystemItemProvider, new SelectItemHandler() {
							public void onSelect(FileSystemItem selected) {
								fileSystemService.move(items,
										(Folder) selected, createListener(
												action, cb));
							}

							public boolean isItemAllowed(FileSystemItem item,
									List<Folder> path) {
								if (item.isFile())
									return false;
								return canMoveTo(items, (Folder) item);
							}
						}, source);
				return;
			}

			if (!canMoveTo(items, folder)) {
				dialogManager.showInfo(textProvider.getStrings()
						.moveMultipleItemsTitle(), textProvider.getStrings()
						.cannotMoveAllItemsMessage());
				return;
			}

			fileSystemService.move(items, folder, createListener(action, cb));
		} else if (action.equals(FileSystemAction.download_as_zip)) {
			fileSystemService.getDownloadAsZipUrl(items,
					new ResultListener<String>() {
						@Override
						public void onFail(ServiceError error) {
							dialogManager.showError(error);
						}

						@Override
						public void onSuccess(String url) {
							cb.onCallback();
							windowManager.openDownloadUrl(url);
						}
					});
		}
	}

	private boolean canCopyTo(List<FileSystemItem> items, Folder target) {
		for (FileSystemItem item : items) {
			if (!canCopyTo(item, target))
				return false;
		}
		return true;
	}

	private boolean canCopyTo(FileSystemItem item, Folder folder) {
		if (item.getParentId().equals(folder.getId()))
			return false;
		if (!item.isFile() && item.getRootId().equals(folder.getRootId())
				&& folder.getPath().startsWith(item.getPath()))
			return false;
		return true;
	}

	private boolean canMoveTo(List<FileSystemItem> items, Folder target) {
		for (FileSystemItem item : items) {
			if (!canMoveTo(item, target))
				return false;
		}
		return true;
	}

	private boolean canMoveTo(FileSystemItem item, Folder folder) {
		if (item.isFile()) {
			// cannot move to its current location
			if (item.getParentId().equals(folder.getId()))
				return false;
		} else {
			// cannot move to itself
			if (item.getId().equals(folder.getId()))
				return false;

			if (item.getRootId().equals(folder.getRootId())) {
				String targetPath = folder.getPath();
				String itemPath = item.getPath();
				return (!targetPath.startsWith(itemPath));
			}
		}

		return true;
	}

	private void onFileAction(final File file, FileSystemAction action,
			Widget source, Object param) {
		if (action.equals(FileSystemAction.view)) {
			JsObj viewParams = (JsObj) param;
			fileViewerFactory.openFileViewer(file, viewParams);
		} else if (action.equals(FileSystemAction.publicLink)) {
			dialogManager.showInfo(textProvider.getStrings()
					.filePublicLinkTitle(), textProvider.getMessages()
					.publicLinkMessage(file.getName()), fileSystemService
					.getPublicLink(file));
		} else if (action.equals(FileSystemAction.download)) {
			windowManager.openDownloadUrl(fileSystemService.getDownloadUrl(
					file, session.getSessionId()));
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
								return canCopyTo(file, (Folder) item);
							}
						}, source);
			} else if (FileSystemAction.copyHere.equals(action)) {
				dialogManager.showInputDialog(textProvider.getStrings()
						.copyHereDialogTitle(), textProvider.getMessages()
						.copyHereDialogMessage(file.getName()), file.getName(),
						new InputListener() {
							@Override
							public boolean isInputAcceptable(String input) {
								return !input.isEmpty()
										&& !file.getName().equals(input);
							}

							@Override
							public void onInput(String name) {
								fileSystemService.copyWithName(file, name,
										createListener(FileSystemAction.copy,
												null));
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
								return canMoveTo(file, (Folder) item);
							}
						}, source);
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
		} else if (action.equals(FileSystemAction.copy)) {
			itemSelectorFactory.openFolderSelector(textProvider.getStrings()
					.copyDirectoryDialogTitle(), textProvider.getMessages()
					.copyDirectoryMessage(folder.getName()), textProvider
					.getStrings().copyDirectoryDialogAction(),
					fileSystemItemProvider, new SelectItemHandler() {
						public void onSelect(FileSystemItem selected) {
							copyFolder(folder, (Folder) selected);
						}

						public boolean isItemAllowed(FileSystemItem candidate,
								List<Folder> path) {
							if (candidate.isFile())
								return false;
							return canCopyTo(folder, (Folder) candidate);
						}
					}, source);

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
							return canMoveTo(folder, (Folder) candidate);
						}
					}, source);
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
		fileSystemService.rename(item, newName, createListener(
				FileSystemAction.rename, null));
	}

	protected void copyFile(File file, Folder toDirectory) {
		if (toDirectory.getId().equals(file.getParentId()))
			return;
		fileSystemService.copy(file, toDirectory, createListener(
				FileSystemAction.copy, null));
	}

	protected void moveFile(File file, Folder toDirectory) {
		if (toDirectory.getId().equals(file.getParentId()))
			return;
		fileSystemService.move(file, toDirectory, createListener(
				FileSystemAction.move, null));
	}

	protected void copyFolder(Folder directory, Folder toDirectory) {
		if (directory.equals(toDirectory))
			return;
		fileSystemService.copy(directory, toDirectory, createListener(
				FileSystemAction.copy, null));
	}

	protected void moveFolder(Folder directory, Folder toDirectory) {
		if (directory.equals(toDirectory))
			return;
		fileSystemService.move(directory, toDirectory, createListener(
				FileSystemAction.move, null));
	}

	private void delete(FileSystemItem item) {
		fileSystemService.delete(item, createListener(FileSystemAction.delete,
				null));
	}

	private ResultListener createListener(final FileSystemAction action,
			final Callback cb) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			public void onSuccess(Object result) {
				if (cb != null)
					cb.onCallback();
				onFileSystemEvent(action);
			}
		};
	}

	protected void onFileSystemEvent(FileSystemAction action) {
		for (FileSystemActionListener listener : listeners)
			listener.onFileSystemAction(action);
	}

	@Override
	public void addListener(FileSystemActionListener listener) {
		listeners.add(listener);
	}
}
