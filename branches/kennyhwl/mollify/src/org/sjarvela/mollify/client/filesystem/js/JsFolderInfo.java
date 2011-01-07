/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.js;

import java.util.List;

import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class JsFolderInfo extends JavaScriptObject {
	protected JsFolderInfo() {
	}

	public final native JsArray<JsFolder> getFolders() /*-{
		return this.folders;
	}-*/;

	public final native JsArray<JsFile> getFiles() /*-{
		return this.files;
	}-*/;

	public final FilePermission getPermission() {
		return FilePermission.fromString(getPermissionString());
	}

	private final native String getPermissionString() /*-{
		return this.permission;
	}-*/;

	public static JsFolderInfo create(JsArray<JsFolder> directories,
			JsArray<JsFile> files) {
		JsFolderInfo result = JsFolderInfo.createObject().cast();
		result.putValues(directories, files);
		return result;
	}

	private final native void putValues(JsArray<JsFolder> directories,
			JsArray<JsFile> files) /*-{
		this.directories = directories;
		this.files = files;
	}-*/;

	public final native int getQuota() /*-{
		if (!this["quota"]) return 0;
		return parseInt(this["quota"].quota);
	}-*/;

	public final native int getQuotaUsed() /*-{
		if (!this["quota"]) return 0;
		return parseInt(this["quota"].used);
	}-*/;

	public final List<String> getSharedFrom() {
		return JsUtil.asList(this.getNativeSharedFrom());
	}

	public final List<String> getSharedTo() {
		return JsUtil.asList(this.getNativeSharedTo());
	}

	public final native JsArrayString getNativeSharedFrom() /*-{
		if (!this["shared"]) return [];
		return this["shared"].from;
	}-*/;

	public final native JsArrayString getNativeSharedTo() /*-{
		if (!this["shared"]) return [];
		return this["shared"].to;
	}-*/;
}
