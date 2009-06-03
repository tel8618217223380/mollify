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

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.ui.WindowManager;

public class DefaultFileSystemActionHandlerFactory implements
		FileSystemActionHandlerFactory {
	private WindowManager windowManager;
	private FileSystemService fileSystemService;
	private DirectoryProvider directoryProvider;

	public DefaultFileSystemActionHandlerFactory(WindowManager windowManager,
			FileSystemService fileSystemService,
			DirectoryProvider directoryProvider) {
		this.windowManager = windowManager;
		this.fileSystemService = fileSystemService;
		this.directoryProvider = directoryProvider;
	}

	public FileSystemActionHandler create(Callback actionCallback) {
		return new DefaultFileSystemActionHandler(windowManager,
				fileSystemService, directoryProvider, actionCallback);
	}

}
