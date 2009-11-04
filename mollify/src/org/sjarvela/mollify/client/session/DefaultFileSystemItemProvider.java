/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultFileSystemItemProvider implements FileSystemItemProvider {
	private final FileSystemService fileSystemService;
	private final SessionProvider sessionProvider;

	private List<Directory> roots = new ArrayList();

	@Inject
	public DefaultFileSystemItemProvider(SessionProvider sessionProvider,
			ServiceEnvironment env) {
		this.sessionProvider = sessionProvider;
		this.sessionProvider.setSessionListener(new SessionListener() {
			public void onSessionChanged() {
				updateRootFolders();
			}
		});

		this.fileSystemService = env.getFileSystemService();
	}

	protected void updateRootFolders() {
		this.roots = sessionProvider.getSession().getRootDirectories();
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

	public void getFilesAndFolders(Directory parent,
			ResultListener<DirectoryContent> listener) {
		if (parent.isEmpty())
			listener.onSuccess(new DirectoryContent(roots,
					Collections.EMPTY_LIST));
		else
			fileSystemService.getDirectoryContents(parent, listener);
	}

}
