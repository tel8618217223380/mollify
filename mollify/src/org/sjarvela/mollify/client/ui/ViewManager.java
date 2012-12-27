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

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

public interface ViewManager {

	void setViewHandlers(JavaScriptObject handlers);

	JsObj getViewHandler(String name);

	void render(ViewHandler view);

	/* old -> */
	// void openView(Widget view);

	void empty();

	void openDownloadUrl(String url);

	void openUrlInNewWindow(String url);

	void showPlainError(String error);

	void showErrorInMainView(String title, ServiceError error);

	RootPanel getRootPanel();

//	Panel getHiddenPanel();

}