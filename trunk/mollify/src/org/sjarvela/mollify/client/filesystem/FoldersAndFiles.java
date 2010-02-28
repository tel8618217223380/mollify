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

import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsFile;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FoldersAndFiles extends JavaScriptObject {
	protected FoldersAndFiles() {
	}

	public final native JsArray<JsFolder> getFolders() /*-{
		return this.folders;
	}-*/;

	public final native JsArray<JsFile> getFiles() /*-{
		return this.files;
	}-*/;

	public static FoldersAndFiles create(JsArray<JsFolder> directories,
			JsArray<JsFile> files) {
		FoldersAndFiles result = FoldersAndFiles.createObject().cast();
		result.putValues(directories, files);
		return result;
	}

	private final native void putValues(JsArray<JsFolder> directories,
			JsArray<JsFile> files) /*-{
		this.directories = directories;
		this.files = files;
	}-*/;
}
