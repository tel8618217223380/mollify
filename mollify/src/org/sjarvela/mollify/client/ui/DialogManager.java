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
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.ErrorValue;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.RenameHandler;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.dialog.ConfirmationDialog;
import org.sjarvela.mollify.client.ui.dialog.FileUploadDialog;
import org.sjarvela.mollify.client.ui.dialog.InfoDialog;
import org.sjarvela.mollify.client.ui.dialog.LoginDialog;
import org.sjarvela.mollify.client.ui.dialog.LoginHandler;
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

	public void showRenameDialog(File file, RenameHandler fileHandler,
			ResultListener listener) {
		new RenameDialog(file, localizator, fileHandler, listener);
	}

	public void openUploadDialog(Directory directory,
			FileActionProvider fileActionProvider, FileUploadHandler fileHandler) {
		new FileUploadDialog(directory, localizator, fileActionProvider,
				fileHandler);
	}

	public void openProgressDialog() {
		new ProgressDialog("Testi");
	}

	public void showError(ServiceError error) {
		new InfoDialog(localizator, localizator.getStrings()
				.infoDialogErrorTitle(), error.getMessage(localizator),
				StyleConstants.INFO_DIALOG_TYPE_ERROR);
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
