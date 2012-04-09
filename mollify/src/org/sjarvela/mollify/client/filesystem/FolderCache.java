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
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class FolderCache implements FolderProvider {
	private final FolderProvider directoryProvider;
	private final Map<JsFolder, List<JsFolder>> cache = new HashMap();

	public FolderCache(FolderProvider directoryProvider) {
		this.directoryProvider = directoryProvider;
	}

	@Override
	public List<JsFolder> getRootFolders() {
		return directoryProvider.getRootFolders();
	}

	@Override
	public JsFolder getRootFolder(String id) {
		return directoryProvider.getRootFolder(id);
	}

	@Override
	public void getFolders(final JsFolder parent,
			final ResultListener<List<JsFolder>> listener) {
		if (cache.containsKey(parent)) {
			listener.onSuccess(cache.get(parent));
			return;
		}

		directoryProvider.getFolders(parent,
				new ResultListener<List<JsFolder>>() {
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					public void onSuccess(List<JsFolder> result) {
						cache.put(parent, result);
						listener.onSuccess(result);
					}
				});
	}
}
