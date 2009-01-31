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

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.file.DirectoryActionHandler;
import org.sjarvela.mollify.client.file.FileOperationHandler;
import org.sjarvela.mollify.client.file.FileSystemAction;
import org.sjarvela.mollify.client.service.FileServices;
import org.sjarvela.mollify.client.service.ProxyResultListener;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.ui.WindowManager;

public class DirectoryActionHandlerImpl implements DirectoryActionHandler {

	private final FileServices fileServices;
	private final WindowManager windowManager;
	private final FileOperationHandler operator;

	private ProxyResultListener renameListener;
	private ProxyResultListener deleteListener;

	public DirectoryActionHandlerImpl(FileServices fileServices,
			FileOperationHandler operator, WindowManager windowManager) {
		this.fileServices = fileServices;
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

	public void onDirectoryAction(Directory directory, FileSystemAction action) {
		if (action.equals(FileSystemAction.RENAME)) {
			windowManager.getDialogManager().showRenameDialog(directory,
					operator, renameListener);
		} else if (action.equals(FileSystemAction.DELETE)) {
			// String title = windowManager.getLocalizator().getStrings()
			// .deleteFileConfirmationDialogTitle();
			// String message = windowManager.getLocalizator().getMessages()
			// .confirmFileDeleteMessage(file.getName());
			// windowManager.getDialogManager().showConfirmationDialog(title,
			// message,
			// StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE_FILE,
			// new ConfirmationListener() {
			// public void onConfirm() {
			// fileOperator.onDelete(file, deleteListener);
			// }
			// });
		} else {
			windowManager.getDialogManager().showInfo("ERROR",
					"Unsupported directory action:" + action.name());
		}
	}

}
