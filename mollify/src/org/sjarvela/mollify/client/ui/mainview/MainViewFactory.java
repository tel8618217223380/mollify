/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.LogoutListener;
import org.sjarvela.mollify.client.TextProvider;
import org.sjarvela.mollify.client.data.SessionInfo;
import org.sjarvela.mollify.client.file.FileSystemActionHandler;
import org.sjarvela.mollify.client.file.FileSystemActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.impl.FileActionProviderImpl;
import org.sjarvela.mollify.client.file.impl.FileSystemActionHandlerImpl;
import org.sjarvela.mollify.client.file.impl.FileUploadHandlerImpl;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.FileServices;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.contextpopup.directorycontext.DirectoryContextPopupFactory;
import org.sjarvela.mollify.client.ui.contextpopup.filecontext.FileContextPopupFactory;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelectorFactory;

public class MainViewFactory {
	private MollifyService service;
	private Localizator localizator;
	private final TextProvider textProvider;

	public MainViewFactory(Localizator localizator, TextProvider textProvider,
			MollifyService service) {
		this.localizator = localizator;
		this.textProvider = textProvider;
		this.service = service;
	}

	public MainView createMainView(WindowManager windowManager,
			SessionInfo info, LogoutListener logoutListener) {
		FileSystemActionProvider actionProvider = new FileActionProviderImpl(
				service);
		FileServices fileServices = new FileServices(service);
		MainViewModel model = new MainViewModel(fileServices, info);

		FileUploadHandler fileUploadHandler = new FileUploadHandlerImpl(service);
		FileSystemActionHandler actionHandler = new FileSystemActionHandlerImpl(
				actionProvider, fileServices, windowManager);
		DirectorySelectorFactory directorySelectorFactory = new DirectorySelectorFactory(
				model, fileServices, localizator);
		FileContextPopupFactory fileContextPopupFactory = new FileContextPopupFactory(
				actionHandler, fileServices, localizator);
		DirectoryContextPopupFactory directoryContextPopupFactory = new DirectoryContextPopupFactory(
				localizator, fileServices, actionHandler, model
						.getSessionInfo().getSettings()
						.isFolderActionsEnabled());
		ActionDelegator actionDelegator = new ActionDelegator();

		// create view, presenter and glue
		MainView view = new MainView(model, textProvider, localizator,
				actionDelegator, directorySelectorFactory,
				fileContextPopupFactory, directoryContextPopupFactory);
		MainViewPresenter presenter = new MainViewPresenter(windowManager,
				model, view, actionProvider, actionHandler, fileUploadHandler,
				fileServices, localizator, logoutListener);
		directorySelectorFactory.setController(presenter);
		new MainViewGlue(view, presenter, actionDelegator);

		return view;
	}
}
