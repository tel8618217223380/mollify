/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.json.JsonRpcHandler;

public class FileService {
	private String baseUrl;

	enum Action {
		get, download
	};

	enum GetType {
		files, dirs, roots
	};

	public FileService() {
		this.baseUrl = "http://localhost:7777/filemanager/service.php";
	}

	public void getFiles(ResultListener resultListener, String dir) {
		getTypes(resultListener, GetType.files, "dir=" + dir);
	}

	public void getDirectories(ResultListener resultListener, String dir) {
		getTypes(resultListener, GetType.dirs, "dir=" + dir);
	}

	public void getRootDirectories(ResultListener resultListener) {
		getTypes(resultListener, GetType.roots);
	}

	public String getDownloadLink(File file) {
		return getUrl(Action.download, "id=" + file.getId());
	}

	/* Utility functions */

	private void getTypes(ResultListener resultListener, GetType type) {
		getTypes(resultListener, type, "");
	}

	private void getTypes(ResultListener resultListener, GetType type,
			String param) {
		ObjectListListener listener = new ObjectListListener(resultListener);
		new JsonRpcHandler(getUrl(Action.get, "type=" + type, param), listener)
				.doRequest();
	}

	private String getUrl(Action action, String... params) {
		String url = baseUrl + "?action=" + action.name();
		for (String param : params) {
			url += "&" + param;
		}
		return url;
	}
}