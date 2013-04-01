/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filesystem;

import java.util.List;

import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemEvent;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.js.JsFile;
import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.ConfirmationListener;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dialog.InputListener;

public class DefaultFileSystemActionHandler implements FileSystemActionHandler {
	private final EventDispatcher eventDispatcher;
	private final ViewManager viewManager;
	private final DialogManager dialogManager;
	private final FileSystemService fileSystemService;
	private final TextProvider textProvider;
	private final SessionInfo session;

	// private final List<FileSystemActionListener> listeners = new ArrayList();

	public DefaultFileSystemActionHandler(EventDispatcher eventDispatcher,
			TextProvider textProvider, ViewManager viewManager,
			DialogManager dialogManager, FileSystemService fileSystemService,
			FileSystemItemProvider fileSystemItemProvider, SessionInfo session) {
		this.eventDispatcher = eventDispatcher;
		this.textProvider = textProvider;
		this.viewManager = viewManager;
		this.dialogManager = dialogManager;
		this.fileSystemService = fileSystemService;
		this.session = session;
	}

	public void onAction(JsFilesystemItem item, FileSystemAction action,
			Object param) {
		if (item.isFile())
			onFileAction((JsFile) item.cast(), action, param);
		else
			onFolderAction((JsFolder) item.cast(), action, param);
	}

	public void onAction(final List<JsFilesystemItem> items,
			final FileSystemAction action, JsFolder folder) {
		if (FileSystemAction.delete.equals(action)) {
			String title = textProvider
					.getText(Texts.deleteFileConfirmationDialogTitle);
			String message = textProvider.getText(
					Texts.confirmMultipleItemDeleteMessage,
					String.valueOf(items.size()));
			dialogManager.showConfirmationDialog(title, message,
					new ConfirmationListener() {
						public void onConfirm() {
							fileSystemService.delete(items,
									createListener(items, action));
						}
					});
		} else if (FileSystemAction.copy.equals(action)) {
			if (folder == null) {
				dialogManager.openFolderSelector(textProvider
						.getText(Texts.copyMultipleItemsTitle), textProvider
						.getText(Texts.copyMultipleItemsMessage,
								String.valueOf(items.size())), textProvider
						.getText(Texts.copyFileDialogAction),
						new SelectFolderHandler() {
							@Override
							public void onSelect(JsFolder selected) {
								fileSystemService.copy(items,
										(JsFolder) selected,
										createListener(items, action));

							}

							@Override
							public boolean canSelect(JsFolder folder) {
								return canCopyTo(items, folder);
							}
						});
				return;
			}

			if (!canCopyTo(items, folder)) {
				dialogManager.showInfo(
						textProvider.getText(Texts.copyMultipleItemsTitle),
						textProvider.getText(Texts.cannotCopyAllItemsMessage));
				return;
			}

			fileSystemService
					.copy(items, folder, createListener(items, action));
		} else if (FileSystemAction.move.equals(action)) {
			if (folder == null) {
				dialogManager.openFolderSelector(textProvider
						.getText(Texts.moveMultipleItemsTitle), textProvider
						.getText(Texts.moveMultipleItemsMessage,
								String.valueOf(items.size())), textProvider
						.getText(Texts.moveFileDialogAction),
						new SelectFolderHandler() {
							@Override
							public void onSelect(JsFolder selected) {
								fileSystemService.move(items,
										(JsFolder) selected,
										createListener(items, action));

							}

							@Override
							public boolean canSelect(JsFolder folder) {
								return canMoveTo(items, folder);
							}
						});
				return;
			}

			if (!canMoveTo(items, folder)) {
				dialogManager.showInfo(
						textProvider.getText(Texts.moveMultipleItemsTitle),
						textProvider.getText(Texts.cannotMoveAllItemsMessage));
				return;
			}

			fileSystemService
					.move(items, folder, createListener(items, action));
		} else if (action.equals(FileSystemAction.download_as_zip)) {
			fileSystemService.getDownloadAsZipUrl(items,
					new ResultListener<String>() {
						@Override
						public void onFail(ServiceError error) {
							dialogManager.showError(error);
						}

						@Override
						public void onSuccess(String url) {
							viewManager.openDownloadUrl(url);
						}
					});
		}
	}

	private boolean canCopyTo(List<JsFilesystemItem> items, JsFolder target) {
		for (JsFilesystemItem item : items) {
			if (!canCopyTo(item, target))
				return false;
		}
		return true;
	}

	private boolean canCopyTo(JsFilesystemItem item, JsFolder folder) {
		if (item.getParentId().equals(folder.getId()))
			return false;
		if (!item.isFile() && item.getRootId().equals(folder.getRootId())
				&& folder.getPath().startsWith(item.getPath()))
			return false;
		return true;
	}

	private boolean canMoveTo(List<JsFilesystemItem> items, JsFolder target) {
		for (JsFilesystemItem item : items) {
			if (!canMoveTo(item, target))
				return false;
		}
		return true;
	}

	private boolean canMoveTo(JsFilesystemItem item, JsFolder folder) {
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

	private void onFileAction(final JsFile file, FileSystemAction action,
			Object param) {
		if (action.equals(FileSystemAction.download)) {
			viewManager.openDownloadUrl(fileSystemService.getDownloadUrl(file,
					session.getSessionId()));
		} else if (action.equals(FileSystemAction.download_as_zip)) {
			viewManager.openDownloadUrl(fileSystemService
					.getDownloadAsZipUrl((JsFilesystemItem) file.cast()));
		} else if (action.equals(FileSystemAction.rename)) {
			if (param != null) {
				rename((JsFilesystemItem) file.cast(), (String) param);
				return;
			}

			dialogManager.showInputDialog(
					textProvider.getText(Texts.renameDialogTitleFile),
					textProvider.getText(Texts.renameDialogNewName,
							file.getName()), file.getName(),
					textProvider.getText(Texts.renameDialogRenameButton),
					textProvider.getText(Texts.dialogCancelButton),
					new InputListener() {
						@Override
						public boolean isInputAcceptable(String input) {
							return !input.isEmpty()
									&& !file.getName().equals(input);
						}

						@Override
						public void onInput(String name) {
							rename((JsFilesystemItem) file.cast(), name);
						}
					});
		} else {
			if (action.equals(FileSystemAction.copy)) {
				if (param != null) {
					copyFile(file, (JsFolder) param);
					return;
				}
				
				dialogManager.openFolderSelector(
						textProvider.getText(Texts.copyFileDialogTitle),
						textProvider.getText(Texts.copyFileMessage,
								file.getName()),
						textProvider.getText(Texts.copyFileDialogAction),
						new SelectFolderHandler() {
							@Override
							public void onSelect(JsFolder selected) {
								copyFile(file, selected);
							}

							@Override
							public boolean canSelect(JsFolder folder) {
								return canCopyTo(
										(JsFilesystemItem) file.cast(), folder);
							}
						});
			} else if (FileSystemAction.copyHere.equals(action)) {
				if (param != null) {
					fileSystemService.copyWithName(
							file,
							(String) param,
							createListener((JsFilesystemItem) file.cast(),
									FileSystemAction.copy));
					return;
				}

				dialogManager.showInputDialog(
						textProvider.getText(Texts.copyHereDialogTitle),
						textProvider.getText(Texts.copyHereDialogMessage,
								file.getName()), file.getName(),
						textProvider.getText(Texts.copyFileDialogAction),
						textProvider.getText(Texts.dialogCancelButton),
						new InputListener() {
							@Override
							public boolean isInputAcceptable(String input) {
								return !input.isEmpty()
										&& !file.getName().equals(input);
							}

							@Override
							public void onInput(String name) {
								fileSystemService.copyWithName(
										file,
										name,
										createListener(
												(JsFilesystemItem) file.cast(),
												FileSystemAction.copy));
							}
						});
			} else if (action.equals(FileSystemAction.move)) {
				if (param != null) {
					moveFile(file, (JsFolder) param);
					return;
				}
				
				dialogManager.openFolderSelector(
						textProvider.getText(Texts.moveFileDialogTitle),
						textProvider.getText(Texts.moveFileMessage,
								file.getName()),
						textProvider.getText(Texts.moveFileDialogAction),
						new SelectFolderHandler() {
							@Override
							public void onSelect(JsFolder selected) {
								moveFile(file, selected);
							}

							@Override
							public boolean canSelect(JsFolder folder) {
								return canMoveTo(
										(JsFilesystemItem) file.cast(), folder);
							}
						});
			} else if (action.equals(FileSystemAction.delete)) {
				String title = textProvider
						.getText(Texts.deleteFileConfirmationDialogTitle);
				String message = textProvider.getText(
						Texts.confirmFileDeleteMessage, file.getName());
				dialogManager.showConfirmationDialog(title, message,
						new ConfirmationListener() {
							public void onConfirm() {
								delete((JsFilesystemItem) file.cast());
							}
						});
			} else {
				dialogManager.showInfo("ERROR",
						"Unsupported action:" + action.name());
			}
		}
	}

	private void onFolderAction(final JsFolder folder, FileSystemAction action,
			Object param) {
		if (action.equals(FileSystemAction.download_as_zip)) {
			viewManager.openDownloadUrl(fileSystemService
					.getDownloadAsZipUrl((JsFilesystemItem) folder.cast()));
		} else if (action.equals(FileSystemAction.rename)) {
			dialogManager.showInputDialog(
					textProvider.getText(Texts.renameDialogTitleDirectory),
					textProvider.getText(Texts.renameDialogNewName,
							folder.getName()), folder.getName(),
					textProvider.getText(Texts.renameDialogRenameButton),
					textProvider.getText(Texts.dialogCancelButton),
					new InputListener() {
						@Override
						public boolean isInputAcceptable(String input) {
							return !input.isEmpty()
									&& !folder.getName().equals(input);
						}

						@Override
						public void onInput(String name) {
							rename((JsFilesystemItem) folder.cast(), name);
						}
					});
		} else if (action.equals(FileSystemAction.copy)) {
			dialogManager.openFolderSelector(
					textProvider.getText(Texts.copyDirectoryDialogTitle),
					textProvider.getText(Texts.copyDirectoryMessage,
							folder.getName()),
					textProvider.getText(Texts.copyDirectoryDialogAction),
					new SelectFolderHandler() {
						@Override
						public void onSelect(JsFolder selected) {
							copyFolder(folder, selected);
						}

						@Override
						public boolean canSelect(JsFolder selected) {
							return canCopyTo((JsFilesystemItem) folder.cast(),
									selected);
						}
					});
		} else if (action.equals(FileSystemAction.move)) {
			dialogManager.openFolderSelector(
					textProvider.getText(Texts.moveDirectoryDialogTitle),
					textProvider.getText(Texts.moveDirectoryMessage,
							folder.getName()),
					textProvider.getText(Texts.moveDirectoryDialogAction),
					new SelectFolderHandler() {
						@Override
						public void onSelect(JsFolder selected) {
							moveFolder(folder, selected);
						}

						@Override
						public boolean canSelect(JsFolder selected) {
							return canMoveTo((JsFilesystemItem) folder.cast(),
									selected);
						}
					});
		} else if (action.equals(FileSystemAction.delete)) {
			String title = textProvider
					.getText(Texts.deleteDirectoryConfirmationDialogTitle);
			String message = textProvider.getText(
					Texts.confirmDirectoryDeleteMessage, folder.getName());
			dialogManager.showConfirmationDialog(title, message,
					new ConfirmationListener() {
						public void onConfirm() {
							delete((JsFilesystemItem) folder.cast());
						}
					});
		} else {
			dialogManager.showInfo("ERROR",
					"Unsupported action:" + action.name());
		}
	}

	public void rename(JsFilesystemItem item, String newName) {
		fileSystemService.rename(item, newName,
				createListener(item, FileSystemAction.rename));
	}

	protected void copyFile(JsFile file, JsFolder toDirectory) {
		if (toDirectory.getId().equals(file.getParentId()))
			return;
		fileSystemService.copy(
				(JsFilesystemItem) file.cast(),
				toDirectory,
				createListener((JsFilesystemItem) file.cast(),
						FileSystemAction.copy));
	}

	protected void moveFile(JsFile file, JsFolder toFolder) {
		if (toFolder.getId().equals(file.getParentId()))
			return;
		fileSystemService.move(
				(JsFilesystemItem) file.cast(),
				toFolder,
				createListener((JsFilesystemItem) file.cast(),
						FileSystemAction.move));
	}

	protected void copyFolder(JsFolder folder, JsFolder toFolder) {
		if (folder.equals(toFolder))
			return;
		fileSystemService.copy(
				(JsFilesystemItem) folder.cast(),
				toFolder,
				createListener((JsFilesystemItem) folder.cast(),
						FileSystemAction.copy));
	}

	protected void moveFolder(JsFolder folder, JsFolder toFolder) {
		if (folder.equals(toFolder))
			return;
		fileSystemService.move(
				(JsFilesystemItem) folder.cast(),
				toFolder,
				createListener((JsFilesystemItem) folder.cast(),
						FileSystemAction.move));
	}

	private void delete(JsFilesystemItem item) {
		fileSystemService.delete(item,
				createListener(item, FileSystemAction.delete));
	}

	private ResultListener createListener(final JsFilesystemItem item,
			final FileSystemAction action) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			public void onSuccess(Object result) {
				eventDispatcher.onEvent(FileSystemEvent.createEvent(item,
						action));
				// onFileSystemEvent(action);
			}
		};
	}

	private ResultListener createListener(final List<JsFilesystemItem> items,
			final FileSystemAction action) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			public void onSuccess(Object result) {
				eventDispatcher.onEvent(FileSystemEvent.createEvent(items,
						action));
				// onFileSystemEvent(action);
			}
		};
	}

	// protected void onFileSystemEvent(FileSystemAction action) {
	// for (FileSystemActionListener listener : listeners)
	// listener.onFileSystemAction(action);
	// }

	// @Override
	// public void addListener(FileSystemActionListener listener) {
	// listeners.add(listener);
	// }
}
