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

import org.sjarvela.mollify.client.data.SessionInfo;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.impl.FileActionHandlerImpl;
import org.sjarvela.mollify.client.file.impl.FileActionProviderImpl;
import org.sjarvela.mollify.client.file.impl.FileUploadHandlerImpl;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.FileServices;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.directoryselector.DirectorySelectorFactory;
import org.sjarvela.mollify.client.ui.fileaction.FileDetailsPopupFactory;

public class MainViewFactory {
	private MollifyService service;
	private Localizator localizator;

	public MainViewFactory(Localizator localizator, MollifyService service) {
		super();
		this.localizator = localizator;
		this.service = service;
	}

	public MainView createMainView(WindowManager windowManager, SessionInfo info) {
		FileActionProvider fileActionProvider = new FileActionProviderImpl(
				service);
		FileServices fileServices = new FileServices(service);
		MainViewModel model = new MainViewModel(fileServices, info);
		FileUploadHandler fileUploadHandler = new FileUploadHandlerImpl(service);
		FileActionHandler fileActionHandler = new FileActionHandlerImpl(
				fileActionProvider, fileServices, windowManager);
		DirectorySelectorFactory directorySelectorFactory = new DirectorySelectorFactory(
				model, fileServices, localizator);
		FileDetailsPopupFactory fileDetailsPopupFactory = new FileDetailsPopupFactory(
				fileActionHandler, fileServices, localizator);

		MainView view = new MainView(model, localizator,
				directorySelectorFactory, fileDetailsPopupFactory);
		MainViewPresenter presenter = new MainViewPresenter(windowManager,
				model, view, fileActionProvider, fileActionHandler,
				fileUploadHandler, localizator);
		directorySelectorFactory.setController(presenter);
		new MainViewGlue(view, presenter);
		return view;
	}
}
