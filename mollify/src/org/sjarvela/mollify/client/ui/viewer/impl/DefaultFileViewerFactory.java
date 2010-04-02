/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.viewer.impl;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.viewer.FileViewerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultFileViewerFactory implements FileViewerFactory {
	private final TextProvider textProvider;

	@Inject
	public DefaultFileViewerFactory(TextProvider textProvider) {
		this.textProvider = textProvider;
	}

	@Override
	public void openFileViewer(File file, JsObj viewParams) {
		new FileViewer(textProvider, file.getName(), viewParams
				.getString("url")).center();
	}

}
