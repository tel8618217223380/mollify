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

import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.ui.WindowManager;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint, UncaughtExceptionHandler {

	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(this);

		WindowManager windowManager = new WindowManager(new MollifyService());
		RootPanel.get("mollify").add(windowManager.createMainView());
	}

	public void onUncaughtException(Throwable e) {
		GWT.log("UNCAUGHT", e);
	}
}
