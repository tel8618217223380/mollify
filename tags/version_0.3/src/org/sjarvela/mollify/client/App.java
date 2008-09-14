/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client;

import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.ui.filemanager.FileManagerController;
import org.sjarvela.mollify.client.ui.filemanager.FileManagerModel;
import org.sjarvela.mollify.client.ui.filemanager.FileManagerView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint, UncaughtExceptionHandler {

	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(this);
		
		MollifyService service = new MollifyService();
		FileManagerModel model = new FileManagerModel();
		FileManagerView view = new FileManagerView(model, Localizator
				.getInstance());
		new FileManagerController(service, model, view);

		RootPanel.get("mollify").add(view);
	}

	public void onUncaughtException(Throwable e) {
		GWT.log("UNCAUGHT", e);
	}
}
