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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.handler.DirectoryHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.dropbox.DropBoxFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemDetailsProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.filecontext.FileContextPopupFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.foldercontext.FolderContextPopupFactory;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.folderselector.FolderSelectorFactory;
import org.sjarvela.mollify.client.ui.itemselector.ItemSelectorFactory;
import org.sjarvela.mollify.client.ui.mainview.CreateFolderDialogFactory;
import org.sjarvela.mollify.client.ui.mainview.MainView;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;
import org.sjarvela.mollify.client.ui.mainview.RenameDialogFactory;
import org.sjarvela.mollify.client.ui.password.PasswordDialog;
import org.sjarvela.mollify.client.ui.password.PasswordDialogFactory;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;
import org.sjarvela.mollify.client.ui.viewer.FileViewerFactory;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultMainViewFactory implements MainViewFactory,
		RenameDialogFactory, CreateFolderDialogFactory {
	private static final String SETTING_EXPOSE_FILE_LINKS = "expose-file-links";

	private final ServiceProvider serviceProvider;
	private final TextProvider textProvider;
	private final ViewManager viewManager;
	private final DialogManager dialogManager;
	private final SessionManager sessionManager;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final ItemSelectorFactory itemSelectorFactory;
	private final PermissionEditorViewFactory permissionEditorViewFactory;
	private final FileUploadDialogFactory fileUploadDialogFactory;
	private final PasswordDialogFactory passwordDialogFactory;
	private final DropBoxFactory dropBoxFactory;
	private final DragAndDropManager dragAndDropManager;
	private final ClientSettings settings;
	private final FileViewerFactory fileViewerFactory;
	private final ItemDetailsProvider itemDetailsProvider;

	@Inject
	public DefaultMainViewFactory(TextProvider textProvider,
			ViewManager viewManager, DialogManager dialogManager,
			ServiceProvider serviceProvider, SessionManager sessionManager,
			ClientSettings settings,
			FileSystemItemProvider fileSystemItemProvider,
			ItemSelectorFactory itemSelectorFactory,
			PermissionEditorViewFactory permissionEditorViewFactory,
			FileUploadDialogFactory fileUploadDialogFactory,
			PasswordDialogFactory passwordDialogFactory,
			FileViewerFactory fileViewerFactory, DropBoxFactory dropBoxFactory,
			DragAndDropManager dragAndDropManager,
			ItemDetailsProvider itemDetailsProvider) {
		this.textProvider = textProvider;
		this.viewManager = viewManager;
		this.dialogManager = dialogManager;
		this.serviceProvider = serviceProvider;
		this.sessionManager = sessionManager;
		this.settings = settings;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.itemSelectorFactory = itemSelectorFactory;
		this.permissionEditorViewFactory = permissionEditorViewFactory;
		this.fileUploadDialogFactory = fileUploadDialogFactory;
		this.passwordDialogFactory = passwordDialogFactory;
		this.fileViewerFactory = fileViewerFactory;
		this.dropBoxFactory = dropBoxFactory;
		this.dragAndDropManager = dragAndDropManager;
		this.itemDetailsProvider = itemDetailsProvider;
	}

	public MainView createMainView() {
		SessionInfo session = sessionManager.getSession();

		FileSystemService fileSystemService = serviceProvider
				.getFileSystemService();
		MainViewModel model = new MainViewModel(fileSystemService, session,
				fileSystemItemProvider);

		FolderSelectorFactory directorySelectorFactory = new FolderSelectorFactory(
				model, fileSystemService, textProvider, fileSystemItemProvider);
		ActionDelegator actionDelegator = new ActionDelegator();

		FileItemDragController dragController = new FileItemDragController(
				textProvider);
		dragAndDropManager.addDragController(FileSystemItem.class,
				dragController);

		FileSystemActionHandler fileSystemActionHandler = new DefaultFileSystemActionHandlerFactory(
				textProvider, viewManager, dialogManager, itemSelectorFactory,
				this, fileViewerFactory, fileSystemService,
				fileSystemItemProvider, sessionManager).create();
		DropBox dropBox = dropBoxFactory.createDropBox(fileSystemActionHandler,
				model.getFolderModel());
		FileContextPopupFactory fileContextPopupFactory = new FileContextPopupFactory(
				fileSystemService, textProvider, session, serviceProvider
						.getExternalService(), dropBox, itemDetailsProvider);
		FolderContextPopupFactory directoryContextPopupFactory = new FolderContextPopupFactory(
				textProvider, fileSystemService, session, dropBox);

		boolean exposeFileUrls = settings.getBool(SETTING_EXPOSE_FILE_LINKS,
				false);

		DefaultMainView view = new DefaultMainView(model, textProvider,
				actionDelegator, directorySelectorFactory,
				fileContextPopupFactory, directoryContextPopupFactory,
				dragAndDropManager);
		MainViewPresenter presenter = new MainViewPresenter(dialogManager,
				viewManager, sessionManager, model, view, serviceProvider
						.getConfigurationService(), fileSystemService,
				textProvider, fileSystemActionHandler,
				permissionEditorViewFactory, passwordDialogFactory,
				fileUploadDialogFactory, this, dropBox, exposeFileUrls,
				serviceProvider.getSessionService());
		dragController.setDataProvider(presenter);
		new MainViewGlue(view, presenter, fileSystemActionHandler,
				actionDelegator);

		return view;
	}

	public void openRenameDialog(FileSystemItem item, RenameHandler handler,
			Widget parent) {
		RenameDialog renameDialog = new RenameDialog(item, textProvider,
				handler);
		if (parent != null)
			viewManager.align(renameDialog, parent);
	}

	public void openPasswordDialog(PasswordHandler handler) {
		new PasswordDialog(textProvider, handler);
	}

	public void openCreateFolderDialog(Folder folder,
			DirectoryHandler directoryHandler) {
		new CreateFolderDialog(folder, textProvider, directoryHandler);
	}
}
