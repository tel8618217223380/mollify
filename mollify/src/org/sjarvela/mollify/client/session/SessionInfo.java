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

import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.session.file.FileSystemInfo;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class SessionInfo extends JavaScriptObject {
	public static SessionInfo create(boolean authenticationRequired,
			boolean authenticated, String sessionName, String sessionId,
			String userId, String user, UserPermissionMode permissionMode,
			FeatureInfo settings, FileSystemInfo fileInfo,
			JsArray<JsFolder> roots) {
		SessionInfo result = SessionInfo.createObject().cast();
		result.putValues(authenticationRequired, authenticated, sessionName,
				sessionId, userId, user, permissionMode.getStringValue(),
				settings, fileInfo, roots);
		return result;
	}

	protected SessionInfo() {
	}

	public final String asString() {
		return JsUtil.asJsonString(this);
	}

	public final native boolean isAuthenticationRequired() /*-{
		return this.authentication_required;
	}-*/;

	public final native boolean isAuthenticated() /*-{
		return this.authenticated;
	}-*/;

	public final native String getSessionName() /*-{
		return this.session_name;
	}-*/;

	public final native String getSessionId() /*-{
		return this.session_id;
	}-*/;

	public final native String getLoggedUserId() /*-{
		return this.user_id;
	}-*/;

	public final native String getLoggedUser() /*-{
		return this.username;
	}-*/;

	public final native FeatureInfo getFeatures() /*-{
		return this.features;
	}-*/;

	public final UserPermissionMode getDefaultPermissionMode() {
		if (getDefaultPermissionModeString() == null)
			return UserPermissionMode.None;
		return UserPermissionMode.fromString(getDefaultPermissionModeString()
				.trim().toLowerCase());
	}

	private final native String getDefaultPermissionModeString() /*-{
		return this.default_permission;
	}-*/;

	public final native FileSystemInfo getFileSystemInfo() /*-{
		return this.filesystem;
	}-*/;

	public final List<Folder> getRootDirectories() {
		if (getRootDirectoryList() == null)
			return Collections.EMPTY_LIST;
		return Folder.createFromDirectories(getRootDirectoryList());
	}

	private final native JsArray getRootDirectoryList() /*-{
		return this.folders;
	}-*/;

	private final native void putValues(boolean authenticationRequired,
			boolean authenticated, String sessionName, String sessionId,
			String userId, String user, String permissionMode,
			FeatureInfo features, FileSystemInfo fileSystemInfo,
			JsArray<JsFolder> folders) /*-{
		this.authentication_required = authenticationRequired;
		this.authenticated = authenticated;
		this.session_name = sessionName;
		this.session_id = sessionId;
		this.user_id = userId;
		this.username = user;
		this.default_permission = permissionMode;
		this.features = features;
		this.filesystem = fileSystemInfo;
		this.folders = folders;
	}-*/;
}
