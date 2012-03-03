/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.formatter.impl;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.formatter.PathFormatter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPathFormatter implements PathFormatter {
	private final FileSystemItemProvider fileSystemItemProvider;
	private final SessionProvider sessionProvider;

	@Inject
	public DefaultPathFormatter(FileSystemItemProvider fileSystemItemProvider,
			SessionProvider sessionProvider) {
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.sessionProvider = sessionProvider;
	}

	@Override
	public String format(FileSystemItem item) {
		Folder rootFolder = fileSystemItemProvider.getRootFolder(item
				.getRootId());
		return (rootFolder == null ? "" : rootFolder.getName())
				+ sessionProvider.getSession().getFileSystemInfo()
						.getFolderSeparator() + item.getParentPath();
	}
}
