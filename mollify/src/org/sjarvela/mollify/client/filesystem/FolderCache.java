/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class FolderCache implements FolderProvider {
	private final FolderProvider folderProvider;
	private final Map<Folder, List<Folder>> cache = new HashMap();

	public FolderCache(FolderProvider folderProvider) {
		this.folderProvider = folderProvider;
	}

	@Override
	public List<Folder> getRootFolders() {
		return folderProvider.getRootFolders();
	}

	@Override
	public Folder getRootFolder(String id) {
		return folderProvider.getRootFolder(id);
	}

	@Override
	public void getFolders(final Folder parent,
			final ResultListener<List<Folder>> listener) {
		if (cache.containsKey(parent)) {
			listener.onSuccess(cache.get(parent));
			return;
		}

		folderProvider.getFolders(parent,
				new ResultListener<List<Folder>>() {
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					public void onSuccess(List<Folder> result) {
						cache.put(parent, result);
						listener.onSuccess(result);
					}
				});
	}
}
