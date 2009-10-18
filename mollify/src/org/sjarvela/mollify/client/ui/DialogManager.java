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
import org.sjarvela.mollify.client.filesystem.directorymodel.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.DirectoryHandler;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.data.ErrorValue;
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileItemUserPermissionHandler;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserHandler;
import org.sjarvela.mollify.client.ui.dialog.SelectItemHandler;

public interface DialogManager {

	public abstract void openLoginDialog(LoginHandler loginHandler);

	public abstract void openRenameDialog(FileSystemItem item,
			RenameHandler renameHandler);

	public abstract void openUploadDialog(Directory directory,
			FileUploadListener listener);

	public abstract void openCreateFolderDialog(Directory parentDirectory,
			DirectoryHandler directoryHandler);

	public abstract void openPasswordDialog(PasswordHandler passwordHandler);

	public abstract ProgressDisplayer openProgressDialog(String title,
			boolean progressBarInitiallyVisible);

	public abstract void showError(ServiceError error);

	public abstract void showError(ErrorValue errorResult);

	public abstract void showInfo(String title, String text);

	public abstract void showConfirmationDialog(String title, String message,
			String style, ConfirmationListener listener);

	public abstract void showSelectFolderDialog(String title, String message,
			String actionTitle, FileSystemItemProvider provider,
			SelectItemHandler listener);

	public abstract void showSelectItemDialog(String title, String message,
			String actionTitle, FileSystemItemProvider provider,
			SelectItemHandler listener);

	public abstract void openConfigurationDialog(ConfigurationService service,
			PasswordHandler passwordHandler);

	public abstract void openAddUserDialog(UserHandler handler);

	public abstract void openEditUserDialog(UserHandler handler, User user);

	public abstract void openResetPasswordDialog(User user,
			PasswordHandler handler);

	public abstract void openFilePermissionEditor(
			ConfigurationService configurationService,
			FileSystemService fileSystemService,
			FileSystemItemProvider fileSystemItemProvider, FileSystemItem item);

	public abstract void openAddFileItemUserPermissionDialog(
			FileItemUserPermissionHandler fileItemUserPermissionHandler,
			List<User> availableUsers);

	public abstract void openEditFileItemUserPermissionDialog(
			FileItemUserPermissionHandler fileItemUserPermissionHandler,
			FileItemUserPermission fileItemUserPermission);

}