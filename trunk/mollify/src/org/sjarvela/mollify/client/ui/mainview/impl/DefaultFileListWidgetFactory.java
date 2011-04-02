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

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;

public class DefaultFileListWidgetFactory implements FileListWidgetFactory {

	private final TextProvider textProvider;
	private final DragAndDropManager dragAndDropManager;
	private final boolean grid;
	private final boolean thumbnails;
	private final FileSystemService service;
	private final boolean experimental;

	public DefaultFileListWidgetFactory(TextProvider textProvider,
			DragAndDropManager dragAndDropManager, ClientSettings settings,
			FileSystemService service) {
		this.textProvider = textProvider;
		this.dragAndDropManager = dragAndDropManager;
		this.service = service;
		this.grid = settings.getBool("grid-view", false);
		this.experimental = settings.getBool("experimental-list", false);
		this.thumbnails = settings.getBool("show-thumbnails", false);
	}

	@Override
	public FileListWidget create() {
		if (grid)
			return new DefaultFileListGridWidget(thumbnails, service);
		if (experimental)
			return new CellTableFileList(textProvider);
		return new DefaultFileListWidget(textProvider, dragAndDropManager);
	}

}
