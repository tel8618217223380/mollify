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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.js.JsDirectory;
import org.sjarvela.mollify.client.session.file.FileSystemInfo;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;

public class SessionInfo extends JavaScriptObject {
	public static SessionInfo create(boolean authenticationRequired,
			boolean authenticated, String userId, String user,
			UserPermissionMode permissionMode, SessionSettings settings,
			ConfigurationInfo configurationInfo, FileSystemInfo fileInfo,
			JsArray<JsDirectory> roots) {
		SessionInfo result = SessionInfo.createObject().cast();
		result.putValues(authenticationRequired, authenticated, userId, user,
				permissionMode.getStringValue(), settings, configurationInfo,
				fileInfo, roots);
		return result;
	}

	protected SessionInfo() {
	}

	public final String asString() {
		return new JSONObject(this).toString();
	}

	public final native boolean isAuthenticationRequired() /*-{
		return this.authentication_required;
	}-*/;

	public final native boolean getAuthenticated() /*-{
		return this.authenticated;
	}-*/;

	public final native String getLoggedUserId() /*-{
		return this.user_id;
	}-*/;

	public final native String getLoggedUser() /*-{
		return this.username;
	}-*/;

	public final native SessionSettings getSettings() /*-{
		return this.settings;
	}-*/;

	public final UserPermissionMode getDefaultPermissionMode() {
		return UserPermissionMode.fromString(getDefaultPermissionModeString()
				.trim().toLowerCase());
	}

	private final native String getDefaultPermissionModeString() /*-{
		return this.default_permission_mode;
	}-*/;

	public final native FileSystemInfo getFileSystemInfo() /*-{
		return this.filesystem;
	}-*/;

	public final native ConfigurationInfo getConfigurationInfo() /*-{
		return this.configuration;
	}-*/;

	public final List<Directory> getRootDirectories() {
		return Directory.createFromDirectories(getRootDirectoryList());
	}

	private final native JsArray getRootDirectoryList() /*-{
		return this.roots;
	}-*/;

	private final native void putValues(boolean authenticationRequired,
			boolean authenticated, String userId, String user,
			String permissionMode, SessionSettings settings,
			ConfigurationInfo configurationInfo, FileSystemInfo fileInfo,
			JsArray<JsDirectory> roots) /*-{
		this.authentication_required = authenticationRequired;
		this.authenticated = authenticated;
		this.user_id = userId;
		this.username = user;
		this.default_permission_mode = permissionMode;
		this.settings = settings;
		this.configuration = configurationInfo;
		this.filesystem = fileInfo;
		this.roots = roots;
	}-*/;
}
