/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.editor.impl;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.editor.FileEditorFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultFileEditorFactory implements FileEditorFactory {

	private TextProvider textProvider;
	private ViewManager viewManager;
	private ServiceProvider serviceProvider;

	@Inject
	public DefaultFileEditorFactory(TextProvider textProvider,
			ViewManager viewManager, ServiceProvider serviceProvider) {
		this.textProvider = textProvider;
		this.viewManager = viewManager;
		this.serviceProvider = serviceProvider;
	}

	@Override
	public void openFileEditor(File file, JsObj params) {
		String embeddedUrl = params.getString("embedded");
		String fullUrl = params.getString("full");

		if (embeddedUrl != null) {
			new FileEditor(textProvider, viewManager,
					serviceProvider.getExternalService(), file.getName(),
					embeddedUrl, fullUrl).center();
		} else if (fullUrl != null) {
			viewManager.openUrlInNewWindow(fullUrl);
		}
	}

}
