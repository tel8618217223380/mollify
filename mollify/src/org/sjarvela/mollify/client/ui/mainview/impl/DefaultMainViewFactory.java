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

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.DirectoryHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialogFactory;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelectorFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.directorycontext.DirectoryContextPopupFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.filecontext.FileContextPopupFactory;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.itemselector.ItemSelectorFactory;
import org.sjarvela.mollify.client.ui.mainview.CreateFolderDialogFactory;
import org.sjarvela.mollify.client.ui.mainview.MainView;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;
import org.sjarvela.mollify.client.ui.mainview.RenameDialogFactory;
import org.sjarvela.mollify.client.ui.password.PasswordDialog;
import org.sjarvela.mollify.client.ui.password.PasswordDialogFactory;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultMainViewFactory implements MainViewFactory,
		RenameDialogFactory, CreateFolderDialogFactory {
	private final ServiceEnvironment environment;
	private final TextProvider textProvider;
	private final ViewManager windowManager;
	private final DialogManager dialogManager;
	private final SessionManager sessionManager;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final ItemSelectorFactory itemSelectorFactory;
	private final PermissionEditorViewFactory permissionEditorViewFactory;
	private final FileUploadDialogFactory fileUploadDialogFactory;
	private final ConfigurationDialogFactory configurationDialogFactory;
	private final PasswordDialogFactory passwordDialogFactory;

	@Inject
	public DefaultMainViewFactory(TextProvider textProvider,
			ViewManager windowManager, DialogManager dialogManager,
			ServiceEnvironment environment, SessionManager sessionManager,
			FileSystemItemProvider fileSystemItemProvider,
			ItemSelectorFactory itemSelectorFactory,
			PermissionEditorViewFactory permissionEditorViewFactory,
			FileUploadDialogFactory fileUploadDialogFactory,
			ConfigurationDialogFactory configurationDialogFactory,
			PasswordDialogFactory passwordDialogFactory) {
		this.textProvider = textProvider;
		this.windowManager = windowManager;
		this.dialogManager = dialogManager;
		this.environment = environment;
		this.sessionManager = sessionManager;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.itemSelectorFactory = itemSelectorFactory;
		this.permissionEditorViewFactory = permissionEditorViewFactory;
		this.fileUploadDialogFactory = fileUploadDialogFactory;
		this.configurationDialogFactory = configurationDialogFactory;
		this.passwordDialogFactory = passwordDialogFactory;
	}

	public MainView createMainView() {
		SessionInfo session = sessionManager.getSession();

		FileSystemService fileSystemService = environment
				.getFileSystemService();
		MainViewModel model = new MainViewModel(fileSystemService, session,
				fileSystemItemProvider);

		DirectorySelectorFactory directorySelectorFactory = new DirectorySelectorFactory(
				model, fileSystemService, textProvider, fileSystemItemProvider);
		FileContextPopupFactory fileContextPopupFactory = new FileContextPopupFactory(
				fileSystemService, textProvider, session);
		DirectoryContextPopupFactory directoryContextPopupFactory = new DirectoryContextPopupFactory(
				textProvider, fileSystemService, session);
		FileSystemActionHandlerFactory fileSystemActionHandlerFactory = new DefaultFileSystemActionHandlerFactory(
				textProvider, windowManager, dialogManager,
				itemSelectorFactory, this, fileSystemService,
				fileSystemItemProvider);
		ActionDelegator actionDelegator = new ActionDelegator();

		DefaultMainView view = new DefaultMainView(model, textProvider,
				actionDelegator, directorySelectorFactory,
				fileContextPopupFactory, directoryContextPopupFactory);
		MainViewPresenter presenter = new MainViewPresenter(dialogManager,
				sessionManager, model, view, environment.getSessionService(),
				fileSystemService, textProvider,
				fileSystemActionHandlerFactory, permissionEditorViewFactory,
				passwordDialogFactory, fileUploadDialogFactory, this,
				configurationDialogFactory);
		new MainViewGlue(view, presenter, actionDelegator);

		return view;
	}

	public void openRenameDialog(FileSystemItem item, RenameHandler handler) {
		new RenameDialog(item, textProvider, handler);
	}

	public void openPasswordDialog(PasswordHandler handler) {
		new PasswordDialog(textProvider, handler);
	}

	public void openCreateFolderDialog(Directory folder,
			DirectoryHandler directoryHandler) {
		new CreateFolderDialog(folder, textProvider, directoryHandler);
	}
}
