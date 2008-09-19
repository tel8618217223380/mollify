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

import org.sjarvela.mollify.client.FileAction;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public class MollifyService {
	private String baseUrl;

	enum Action {
		get, operate
	};

	enum GetType {
		details, files, dirs, roots, auth
	};

	public MollifyService() {
		// MollifyService assumes that development environment web server is
		// localhost:7777

		// For a standalone version, it is assumed that backend facade
		// (service.php) is in the same directory than the host html page.

		this.baseUrl = GWT.isScript() ? GWT.getHostPageBaseURL()
				: "http://localhost:7777/mollify/";
		this.baseUrl += "service.php";
	}

	public void checkAuthentication(ResultListener resultListener) {
		getTypes(resultListener, GetType.auth);
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

	public void getFileDetails(File file, ResultListener resultListener) {
		String url = getUrl(Action.get, "type=" + GetType.details, "id="
				+ file.getId());
		doRequest(url, resultListener);
	}

	public void renameFile(File file, String newName,
			ResultListener resultListener) {
		String url = getFileActionUrl(file, FileAction.RENAME) + "&to="
				+ URL.encode(newName);
		doRequest(url, resultListener);
	}

	public void deleteFile(File file, ResultListener resultListener) {
		doRequest(getFileActionUrl(file, FileAction.DELETE), resultListener);
	}

	public String getFileActionUrl(File file, FileAction action) {
		if (action.equals(FileAction.UPLOAD)) {
			throw new RuntimeException("Invalid file action request "
					+ action.name());
		}

		if (file.isEmpty()) {
			throw new RuntimeException("No file given, action " + action.name());
		}

		return getUrl(Action.operate, "type=" + action.name(), "id="
				+ file.getId());
	}

	public String getDirectoryActionUrl(Directory dir, FileAction action) {
		if (!action.equals(FileAction.UPLOAD)) {
			throw new RuntimeException("Invalid directory action request "
					+ action.name());
		}

		return getUrl(Action.operate, "type=" + action.name(), "id="
				+ dir.getId());
	}

	/* Utility functions */

	private void getTypes(ResultListener resultListener, GetType type) {
		getTypes(resultListener, type, "");
	}

	private void getTypes(ResultListener resultListener, GetType type,
			String param) {
		doRequest(getUrl(Action.get, "type=" + type, param), resultListener);
	}

	private String getUrl(Action action, String... params) {
		String url = baseUrl + "?action=" + action.name();
		for (String param : params) {
			url += "&" + param;
		}
		return url;
	}

	private void doRequest(String url, ResultListener resultListener) {
		ResultValidator listener = new ResultValidator(resultListener);
		new JsonRpcHandler(url, listener).doRequest();

	}
}