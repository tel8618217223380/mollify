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
import org.sjarvela.mollify.client.file.FileAction;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileOperationHandler;
import org.sjarvela.mollify.client.service.ProxyResultListener;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.WindowManager;

public class FileActionHandlerImpl implements FileActionHandler {
	private final WindowManager windowManager;
	private final FileActionProvider fileActionProvider;
	private final FileOperationHandler fileOperator;
	private ProxyResultListener renameListener;
	private ProxyResultListener deleteListener;

	public FileActionHandlerImpl(FileActionProvider fileActionProvider,
			FileOperationHandler fileOperator, WindowManager windowManager) {
		super();
		this.fileActionProvider = fileActionProvider;
		this.fileOperator = fileOperator;
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

	public void onFileAction(final File file, FileAction action) {
		if (action.equals(FileAction.DOWNLOAD)) {
			windowManager.openDownloadUrl(fileActionProvider.getActionURL(file,
					action));
		} else if (action.equals(FileAction.RENAME)) {
			windowManager.getDialogManager().showRenameDialog(file,
					fileOperator, renameListener);
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
							fileOperator.onDelete(file, deleteListener);
						}
					});
		} else {
			windowManager.getDialogManager().showInfo("ERROR",
					"Unsupported action:" + action.name());
		}

	}

}
