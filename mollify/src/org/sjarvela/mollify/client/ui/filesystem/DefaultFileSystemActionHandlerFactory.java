/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filesystem;

import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultFileSystemActionHandlerFactory implements
		FileSystemActionHandlerFactory {
	private final TextProvider textProvider;
	private final DialogManager dialogManager;
	private final FileSystemService fileSystemService;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final ViewManager windowManager;
	// private final ItemSelectorFactory itemSelectorFactory;
	// private final RenameDialogFactory renameDialogFactory;
	private final SessionProvider sessionProvider;
	// private final FileViewerFactory fileViewerFactory;
	private final EventDispatcher eventDispatcher;

	// private final FileEditorFactory fileEditorFactory;

	@Inject
	public DefaultFileSystemActionHandlerFactory(
			EventDispatcher eventDispatcher, TextProvider textProvider,
			ViewManager windowManager, DialogManager dialogManager,
			ServiceEnvironment env,
			FileSystemItemProvider fileSystemItemProvider,
			SessionProvider sessionProvider) {
		this.eventDispatcher = eventDispatcher;
		this.textProvider = textProvider;
		this.windowManager = windowManager;
		this.dialogManager = dialogManager;
		// this.itemSelectorFactory = itemSelectorFactory;
		// this.renameDialogFactory = renameDialogFactory;
		// this.fileViewerFactory = fileViewerFactory;
		// this.fileEditorFactory = fileEditorFactory;
		this.fileSystemService = env.getFileSystemService();
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.sessionProvider = sessionProvider;
	}

	@Inject
	public FileSystemActionHandler create() {
		return new DefaultFileSystemActionHandler(eventDispatcher,
				textProvider, windowManager, dialogManager, fileSystemService,
				fileSystemItemProvider, sessionProvider.getSession());
	}

}
