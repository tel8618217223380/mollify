/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.file.impl;

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.file.FileSystemAction;
import org.sjarvela.mollify.client.file.FileSystemActionHandler;
import org.sjarvela.mollify.client.file.FileSystemActionProvider;
import org.sjarvela.mollify.client.file.FileSystemOperationHandler;
import org.sjarvela.mollify.client.service.ProxyResultListener;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.WindowManager;

public class FileSystemActionHandlerImpl implements FileSystemActionHandler {
	private final WindowManager windowManager;
	private final FileSystemActionProvider actionProvider;
	private final FileSystemOperationHandler operator;
	private ProxyResultListener renameListener;
	private ProxyResultListener deleteListener;

	public FileSystemActionHandlerImpl(FileSystemActionProvider actionProvider,
			FileSystemOperationHandler operator, WindowManager windowManager) {
		this.actionProvider = actionProvider;
		this.operator = operator;
		this.windowManager = windowManager;

		this.renameListener = new ProxyResultListener();
		this.deleteListener = new ProxyResultListener();
	}

	public void addRenameListener(ResultListener listener) {
		renameListener.addListener(listener);
	}

	public void addDeleteListener(ResultListener listener) {
		deleteListener.addListener(listener);
	}

	public void onAction(final FileSystemItem item, FileSystemAction action) {
		if (action.equals(FileSystemAction.download)) {
			if (!item.isFile())
				throw new RuntimeException("Invalid action");
			windowManager.openDownloadUrl(actionProvider.getActionURL(
					(File) item, action));
		} else if (action.equals(FileSystemAction.rename)) {
			windowManager.getDialogManager().showRenameDialog(item, operator,
					renameListener);
		} else if (action.equals(FileSystemAction.delete)) {
			String title = item.isFile() ? windowManager.getLocalizator()
					.getStrings().deleteFileConfirmationDialogTitle()
					: windowManager.getLocalizator().getStrings()
							.deleteDirectoryConfirmationDialogTitle();
			String message = item.isFile() ? windowManager.getLocalizator()
					.getMessages().confirmFileDeleteMessage(item.getName())
					: windowManager.getLocalizator().getMessages()
							.confirmDirectoryDeleteMessage(item.getName());
			windowManager.getDialogManager().showConfirmationDialog(title,
					message, StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
					new ConfirmationListener() {
						public void onConfirm() {
							operator.onDelete(item, deleteListener);
						}
					});
		} else {
			windowManager.getDialogManager().showInfo("ERROR",
					"Unsupported action:" + action.name());
		}
	}
}
