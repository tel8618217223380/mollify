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

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.session.file.FilePermission;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

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

	public static JsFolderInfo create(JsArray<JsFolder> folders,
			JsArray<JsFile> files) {
		JsFolderInfo result = JsFolderInfo.createObject().cast();
		result.putValues(folders, files);
		return result;
	}

	private final native void putValues(JsArray<JsFolder> folders,
			JsArray<JsFile> files) /*-{
		this.folders = folders;
		this.files = files;
	}-*/;

	public final native JsObj getData() /*-{
		return this.data;
	}-*/;
}
