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

import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

public class DefaultMainViewFactory implements MainViewFactory {
	// private static final String SETTING_EXPOSE_FILE_LINKS =
	// "expose-file-links";
	// private static final String SETTING_DEFAULT_VIEW_MODE =
	// "default-view-mode";

	private final ServiceProvider serviceProvider;
	private final TextProvider textProvider;
	private final ViewManager viewManager;
	private final DialogManager dialogManager;
	private final SessionManager sessionManager;
	private final FileSystemItemProvider fileSystemItemProvider;
	// private final PermissionEditorViewFactory permissionEditorViewFactory;
	// private final FileUploadDialogFactory fileUploadDialogFactory;
	// private final PasswordDialogFactory passwordDialogFactory;
	// private final DropBoxFactory dropBoxFactory;
	// private final DragAndDropManager dragAndDropManager;
	// private final ClientSettings settings;
	private final EventDispatcher eventDispatcher;
	// private final SearchResultDialogFactory searchResultDialogFactory;
	private final FileSystemActionHandlerFactory fileSystemActionHandlerFactory;

	// private final ItemContextPopupFactory itemContextPopupFactory;

	public DefaultMainViewFactory(EventDispatcher eventDispatcher,
			TextProvider textProvider, ViewManager viewManager,
			DialogManager dialogManager, ServiceProvider serviceProvider,
			SessionManager sessionManager,
			// ClientSettings settings,
			FileSystemItemProvider fileSystemItemProvider,
			// PermissionEditorViewFactory permissionEditorViewFactory,
			// FileUploadDialogFactory fileUploadDialogFactory,
			// PasswordDialogFactory passwordDialogFactory,
			// DropBoxFactory dropBoxFactory,
			// DragAndDropManager dragAndDropManager,
			// SearchResultDialogFactory searchResultDialogFactory,
			FileSystemActionHandlerFactory fileSystemActionHandlerFactory) {
		this.eventDispatcher = eventDispatcher;
		this.textProvider = textProvider;
		this.viewManager = viewManager;
		this.dialogManager = dialogManager;
		this.serviceProvider = serviceProvider;
		this.sessionManager = sessionManager;
		// this.settings = settings;
		this.fileSystemItemProvider = fileSystemItemProvider;
		// this.permissionEditorViewFactory = permissionEditorViewFactory;
		// this.fileUploadDialogFactory = fileUploadDialogFactory;
		// this.passwordDialogFactory = passwordDialogFactory;
		// this.dropBoxFactory = dropBoxFactory;
		// this.dragAndDropManager = dragAndDropManager;
		// this.searchResultDialogFactory = searchResultDialogFactory;
		this.fileSystemActionHandlerFactory = fileSystemActionHandlerFactory;
		// this.itemContextPopupFactory = itemContextPopupFactory;
	}

	@Override
	public void openMainView() {
		SessionInfo session = sessionManager.getSession();

		FileSystemService fileSystemService = serviceProvider
				.getFileSystemService();
		MainViewModel model = new MainViewModel(fileSystemService, session,
				fileSystemItemProvider);
		FileSystemActionHandler fileSystemActionHandler = fileSystemActionHandlerFactory
				.create();
		// boolean exposeFileUrls = settings.getBool(SETTING_EXPOSE_FILE_LINKS,
		// false);

		NativeMainView view = new NativeMainView(
				viewManager.getViewHandler("mainview"));
		new MainViewPresenter(viewManager, dialogManager, sessionManager,
				model, view, serviceProvider.getConfigurationService(),
				fileSystemService, textProvider, fileSystemActionHandler,
				serviceProvider.getSessionService(), eventDispatcher);
		// TODO MainViewGlue glue = new MainViewGlue(view, presenter,
		// fileSystemActionHandler, actionDelegator);
		viewManager.render(view);
	}

	// public MainView createMainView(FileViewDelegate fileViewDelegate) {

	// FolderSelectorFactory folderSelectorFactory = new
	// FolderSelectorFactory(
	// model, fileSystemService, textProvider, fileSystemItemProvider);
	// ActionDelegator actionDelegator = new ActionDelegator();
	//
	// FileItemDragController dragController = new FileItemDragController(
	// textProvider);
	// dragAndDropManager.addDragController(FileSystemItem.class,
	// dragController);

	// FileSystemActionHandler fileSystemActionHandler =
	// fileSystemActionHandlerFactory
	// .create();
	// DropBox dropBox =
	// dropBoxFactory.createDropBox(fileSystemActionHandler,
	// model.getFolderModel());
	// ItemContextPopup itemContextPopup = itemContextPopupFactory
	// .createPopup(dropBox);

	// boolean exposeFileUrls = settings.getBool(SETTING_EXPOSE_FILE_LINKS,
	// false);

	// FileListWidgetFactory fileListViewFactory = new
	// DefaultFileListWidgetFactory(
	// textProvider, dragAndDropManager, settings, fileSystemService,
	// pluginEnvironment);
	// ViewType defaultViewType = getDefaultViewType();
	// DefaultMainView view = new DefaultMainView(model, textProvider,
	// actionDelegator, folderSelectorFactory, itemContextPopup,
	// dropBox, dragAndDropManager, fileListViewFactory,
	// defaultViewType);
	// MainViewPresenter presenter = new MainViewPresenter(dialogManager,
	// viewManager, sessionManager, model, view,
	// serviceProvider.getConfigurationService(), fileSystemService,
	// textProvider, fileSystemActionHandler,
	// permissionEditorViewFactory, passwordDialogFactory,
	// fileUploadDialogFactory, this, dropBox, exposeFileUrls,
	// serviceProvider.getSessionService(), eventDispatcher,
	// searchResultDialogFactory, pluginEnvironment);
	// dragController.setDataProvider(presenter);
	// MainViewGlue glue = new MainViewGlue(view, presenter,
	// fileSystemActionHandler, actionDelegator);
	// fileViewDelegate.setDelegate(glue);
	//
	// return view;
	// }

	/*
	 * TODO private org.sjarvela.mollify.client.ui.mainview.MainView.ViewType
	 * getDefaultViewType() { String setting =
	 * settings.getString(SETTING_DEFAULT_VIEW_MODE); if (setting != null) {
	 * setting = setting.trim().toLowerCase(); if (setting.equals("small-icon"))
	 * return ViewType.gridSmall; if (setting.equals("large-icon")) return
	 * ViewType.gridLarge; } return ViewType.list; }
	 */

	// public void openPasswordDialog(PasswordHandler handler) {
	// new PasswordDialog(textProvider, handler);
	// }
	//
	// public void openCreateFolderDialog(Folder folder,
	// FolderHandler directoryHandler) {
	// new CreateFolderDialog(folder, textProvider, directoryHandler);
	// }
}
