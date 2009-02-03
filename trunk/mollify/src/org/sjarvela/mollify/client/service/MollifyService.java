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

import org.sjarvela.mollify.client.DateTime;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.file.FileActionUrlProvider;
import org.sjarvela.mollify.client.file.FileSystemAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public class MollifyService implements FileActionUrlProvider {
	private String baseUrl;

	enum Action {
		get, operate, auth, session_info, logout
	};

	enum GetType {
		details, files, dirs, dirs_and_files, roots, upload_status
	};

	public MollifyService(String path) {
		// MollifyService assumes that development environment web server is
		// localhost:7777

		// For a standalone version, it is assumed that backend facade
		// (service.php) is in the same directory than the host html page.

		this.baseUrl = GWT.isScript() ? GWT.getHostPageBaseURL()
				: "http://localhost:7777/mollify/";
		this.baseUrl += getOptionalPath(path) + "service.php";
	}

	private String getOptionalPath(String path) {
		if (path == null || path.length() == 0)
			return "";

		String result = path.trim();

		if (path.toLowerCase().startsWith("http://"))
			result = result.substring(7);

		while (true) {
			char c = result.charAt(0);

			if (c == '.' || c == '/')
				result = result.substring(1);
			else
				break;
		}

		if (result.length() > 0 && !result.endsWith("/"))
			result += "/";

		return result;
	}

	public void getSessionInfo(ResultListener resultListener) {
		doRequest(getUrl(Action.session_info), resultListener);
	}

	public void authenticate(String userName, String password,
			final ResultListener resultListener) {
		doRequest(getUrl(Action.auth, "username=" + userName, "password="
				+ MD5.generateMD5(password)), resultListener);
	}

	public void logout(ResultListener resultListener) {
		doRequest(getUrl(Action.logout), resultListener);
	}

	public void getFiles(ResultListener resultListener, String dir) {
		getType(resultListener, GetType.files, "dir=" + dir);
	}

	public void getDirectories(ResultListener resultListener, String dir) {
		getType(resultListener, GetType.dirs, "dir=" + dir);
	}

	public void getDirectoriesAndFiles(ResultListener resultListener, String dir) {
		getType(resultListener, GetType.dirs_and_files, "dir=" + dir);
	}

	public void getRootDirectories(ResultListener resultListener) {
		getType(resultListener, GetType.roots);
	}

	public void getFileDetails(File file, ResultListener resultListener) {
		String url = getUrl(Action.get, "type=" + GetType.details, "id="
				+ file.getId(), getItemTypeParam(file));
		doRequest(url, resultListener);
	}

	public void getDirectoryDetails(Directory directory,
			ResultListener resultListener) {
		String url = getUrl(Action.get, "type=" + GetType.details, "id="
				+ directory.getId(), getItemTypeParam(directory));
		doRequest(url, resultListener);
	}

	public void renameFile(File file, String newName,
			ResultListener resultListener) {
		String url = getActionUrl(file, FileSystemAction.rename) + "&to="
				+ URL.encode(newName);
		doRequest(url, resultListener);
	}

	public void renameDirectory(Directory dir, String newName,
			ResultListener listener) {
		String url = getActionUrl(dir, FileSystemAction.rename) + "&to="
				+ URL.encode(newName);
		doRequest(url, listener);
	}

	public void deleteFile(File file, ResultListener resultListener) {
		doRequest(getActionUrl(file, FileSystemAction.delete), resultListener);
	}

	public void deleteDirectory(Directory dir, ResultListener listener) {
		doRequest(getActionUrl(dir, FileSystemAction.delete), listener);
	}

	public void createFolder(Directory parentFolder, String folderName,
			ResultListener listener) {
		doRequest(getActionUrl(parentFolder, FileSystemAction.create_folder,
				"name=" + folderName), listener);
	}

	public void getUploadProgress(String id, ResultListener resultListener) {
		getType(resultListener, GetType.upload_status, "id=" + id);
	}

	public String getActionUrl(FileSystemItem item, FileSystemAction action) {
		return getActionUrl(item, action, new String[0]);
	}

	public String getActionUrl(FileSystemItem item, FileSystemAction action,
			String... params) {
		if (item.isEmpty()) {
			throw new RuntimeException("No item defined, action "
					+ action.name());
		}
		if (!action.isApplicable(item)) {
			throw new RuntimeException("Invalid action request "
					+ action.name());
		}

		String[] urlParams = new String[params.length + 3];
		urlParams[0] = "type=" + action.name();
		urlParams[1] = "id=" + item.getId();
		urlParams[2] = getItemTypeParam(item);
		for (int i = 0; i < params.length; i++)
			urlParams[i + 3] = params[i];

		return getUrl(Action.operate, urlParams);
	}

	private String getItemTypeParam(FileSystemItem item) {
		return "item_type=" + (item.isFile() ? "f" : "d");
	}

	/* Utility functions */

	private void getType(ResultListener resultListener, GetType type) {
		getType(resultListener, type, "");
	}

	private void getType(ResultListener resultListener, GetType type,
			String param) {
		String params = "type=" + type;
		if (param.length() > 0)
			params += "&" + param;
		doRequest(getUrl(Action.get, params), resultListener);
	}

	private String getUrl(Action action, String... params) {
		String url = baseUrl + "?action=" + action.name();
		for (String param : params)
			url += "&" + param;
		return url;
	}

	private void doRequest(String url, ResultListener resultListener) {
		new JsonRpcHandler(URL.encode(url), resultListener).doRequest();
	}

	// Just any unique id, time in millisecond level is unique enough
	public String getNewUploadId() {
		return DateTime.getInstance().getInternalExactFormat().format(
				DateTime.getInstance().currentTime());
	}

}