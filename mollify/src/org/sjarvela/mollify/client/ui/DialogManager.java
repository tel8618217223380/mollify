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

import java.util.List;

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.filesystem.handler.DirectoryHandler;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.ErrorValue;
import org.sjarvela.mollify.client.session.FileSystemInfo;
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.session.PasswordHandler;
import org.sjarvela.mollify.client.ui.dialog.ConfirmationDialog;
import org.sjarvela.mollify.client.ui.dialog.CreateFolderDialog;
import org.sjarvela.mollify.client.ui.dialog.FileUploadDialog;
import org.sjarvela.mollify.client.ui.dialog.InfoDialog;
import org.sjarvela.mollify.client.ui.dialog.LoginDialog;
import org.sjarvela.mollify.client.ui.dialog.PasswordDialog;
import org.sjarvela.mollify.client.ui.dialog.ProgressDialog;
import org.sjarvela.mollify.client.ui.dialog.RenameDialog;
import org.sjarvela.mollify.client.ui.dialog.SelectFolderDialog;
import org.sjarvela.mollify.client.ui.dialog.SelectFolderListener;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog;

public class DialogManager {
	private TextProvider textProvider;

	public DialogManager(TextProvider textProvider) {
		super();
		this.textProvider = textProvider;
	}

	public void showLoginDialog(LoginHandler loginHandler) {
		new LoginDialog(textProvider, loginHandler);
	}

	public void showRenameDialog(FileSystemItem item,
			RenameHandler renameHandler) {
		new RenameDialog(item, textProvider, renameHandler);
	}

	public void openUploadDialog(Directory directory,
			FileUploadService fileUploadService, FileSystemInfo info,
			FileUploadListener listener) {
		new FileUploadDialog(directory, textProvider, fileUploadService, info,
				listener);
	}

	public void openCreateFolderDialog(Directory parentDirectory,
			DirectoryHandler directoryHandler) {
		new CreateFolderDialog(parentDirectory, textProvider, directoryHandler);
	}

	public void openPasswordDialog(PasswordHandler passwordHandler) {
		new PasswordDialog(textProvider, passwordHandler);
	}

	public ProgressDisplayer openProgressDialog(String title,
			boolean progressBarInitiallyVisible) {
		return new ProgressDialog(title, progressBarInitiallyVisible);
	}

	public void showError(ServiceError error) {
		new InfoDialog(textProvider, textProvider.getStrings()
				.infoDialogErrorTitle(), error.getType().getMessage(
				textProvider), StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showError(ErrorValue errorResult) {
		new InfoDialog(textProvider, textProvider.getStrings()
				.infoDialogErrorTitle(), textProvider
				.getErrorMessage(errorResult),
				StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showInfo(String title, String text) {
		new InfoDialog(textProvider, title, text,
				StyleConstants.INFO_DIALOG_TYPE_INFO);
	}

	public void showConfirmationDialog(String title, String message,
			String style, ConfirmationListener listener) {
		new ConfirmationDialog(textProvider, title, message, style, listener);
	}

	public void showSelectFolderDialog(String title, String message,
			String actionTitle, DirectoryProvider provider,
			SelectFolderListener listener, List<Directory> initialDirectoryPath) {
		new SelectFolderDialog(this, textProvider, title, message, actionTitle,
				provider, listener, initialDirectoryPath);
	}

	public void openConfigurationDialog(SettingsService service) {
		new ConfigurationDialog(textProvider, this, service);
	}

}
