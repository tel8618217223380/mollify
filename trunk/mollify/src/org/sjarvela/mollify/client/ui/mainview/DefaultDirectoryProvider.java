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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class DefaultDirectoryProvider implements DirectoryProvider {
	private final FileSystemService fileSystemService;
	private final List<Directory> roots;

	public DefaultDirectoryProvider(List<Directory> roots,
			FileSystemService fileSystemService) {
		this.roots = roots;
		this.fileSystemService = fileSystemService;
	}

	public void getDirectories(Directory parent,
			ResultListener<List<Directory>> listener) {
		if (parent.isEmpty())
			listener.onSuccess(roots);
		else
			fileSystemService.getDirectories(parent, listener);
	}

	public List<Directory> getRootDirectories() {
		return roots;
	}

}
