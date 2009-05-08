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

import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class DirectoryCache implements DirectoryProvider {

	private final DirectoryProvider directoryProvider;
	private final Map<Directory, List<Directory>> cache = new HashMap();

	public DirectoryCache(DirectoryProvider directoryProvider) {
		this.directoryProvider = directoryProvider;
	}

	public List<Directory> getRootDirectories() {
		return directoryProvider.getRootDirectories();
	}

	public void getDirectories(final Directory parent,
			final ResultListener<List<Directory>> listener) {
		if (cache.containsKey(parent)) {
			listener.onSuccess(cache.get(parent));
			return;
		}

		directoryProvider.getDirectories(parent,
				new ResultListener<List<Directory>>() {
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					public void onSuccess(List<Directory> result) {
						cache.put(parent, result);
						listener.onSuccess(result);
					}
				});
	}
}
