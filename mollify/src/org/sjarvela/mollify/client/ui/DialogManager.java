/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui;

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.LoginHandler;
import org.sjarvela.mollify.client.ProgressListener;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.ErrorValue;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.file.DirectoryHandler;
import org.sjarvela.mollify.client.file.FileActionUrlProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.RenameHandler;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyError;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.ui.dialog.ConfirmationDialog;
import org.sjarvela.mollify.client.ui.dialog.CreateFolderDialog;
import org.sjarvela.mollify.client.ui.dialog.FileUploadDialog;
import org.sjarvela.mollify.client.ui.dialog.InfoDialog;
import org.sjarvela.mollify.client.ui.dialog.LoginDialog;
import org.sjarvela.mollify.client.ui.dialog.ProgressDialog;
import org.sjarvela.mollify.client.ui.dialog.RenameDialog;

public class DialogManager {
	private Localizator localizator;

	public DialogManager(Localizator localizator) {
		super();
		this.localizator = localizator;
	}

	public void showLoginDialog(LoginHandler loginHandler) {
		new LoginDialog(localizator, loginHandler);
	}

	public void showRenameDialog(FileSystemItem item,
			RenameHandler fileHandler, ResultListener listener) {
		new RenameDialog(item, localizator, fileHandler, listener);
	}

	public void openUploadDialog(Directory directory,
			FileActionUrlProvider fileActionProvider, FileUploadHandler fileHandler) {
		new FileUploadDialog(directory, localizator, fileActionProvider,
				fileHandler);
	}

	public void openCreateFolderDialog(Directory parentDirectory,
			DirectoryHandler directoryHandler, ResultListener resultListener) {
		new CreateFolderDialog(parentDirectory, directoryHandler, localizator,
				resultListener);
	}

	public ProgressListener openProgressDialog(String title,
			boolean progressBarInitiallyVisible) {
		return new ProgressDialog(title, progressBarInitiallyVisible);
	}

	public void showError(MollifyError error) {
		new InfoDialog(localizator, localizator.getStrings()
				.infoDialogErrorTitle(), error.getError().getMessage(
				localizator), StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showError(ErrorValue errorResult) {
		new InfoDialog(localizator, localizator.getStrings()
				.infoDialogErrorTitle(), localizator
				.getErrorMessage(errorResult),
				StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showInfo(String title, String text) {
		new InfoDialog(localizator, title, text,
				StyleConstants.INFO_DIALOG_TYPE_INFO);
	}

	public void showConfirmationDialog(String title, String message,
			String style, ConfirmationListener listener) {
		new ConfirmationDialog(localizator, title, message, style, listener);
	}

}
