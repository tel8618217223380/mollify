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
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.itemselector.ItemSelectorFactory;
import org.sjarvela.mollify.client.ui.mainview.RenameDialogFactory;
import org.sjarvela.mollify.client.ui.viewer.FileViewerFactory;

public class DefaultFileSystemActionHandlerFactory implements
		FileSystemActionHandlerFactory {
	private final TextProvider textProvider;
	private final DialogManager dialogManager;
	private final FileSystemService fileSystemService;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final ViewManager windowManager;
	private final ItemSelectorFactory itemSelectorFactory;
	private final RenameDialogFactory renameDialogFactory;
	private final SessionProvider sessionProvider;
	private final FileViewerFactory fileViewerFactory;
	private final EventDispatcher eventDispatcher;

	public DefaultFileSystemActionHandlerFactory(
			EventDispatcher eventDispatcher, TextProvider textProvider,
			ViewManager windowManager, DialogManager dialogManager,
			ItemSelectorFactory itemSelectorFactory,
			RenameDialogFactory renameDialogFactory,
			FileViewerFactory fileViewerFactory,
			FileSystemService fileSystemService,
			FileSystemItemProvider fileSystemItemProvider,
			SessionProvider sessionProvider) {
		this.eventDispatcher = eventDispatcher;
		this.textProvider = textProvider;
		this.windowManager = windowManager;
		this.dialogManager = dialogManager;
		this.itemSelectorFactory = itemSelectorFactory;
		this.renameDialogFactory = renameDialogFactory;
		this.fileViewerFactory = fileViewerFactory;
		this.fileSystemService = fileSystemService;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.sessionProvider = sessionProvider;
	}

	public FileSystemActionHandler create() {
		return new DefaultFileSystemActionHandler(eventDispatcher, textProvider, windowManager,
				dialogManager, itemSelectorFactory, renameDialogFactory,
				fileViewerFactory, fileSystemService, fileSystemItemProvider,
				sessionProvider.getSession());
	}

}
