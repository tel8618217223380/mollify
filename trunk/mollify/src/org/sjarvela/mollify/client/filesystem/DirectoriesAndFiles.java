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

import org.sjarvela.mollify.client.filesystem.js.JsDirectory;
import org.sjarvela.mollify.client.filesystem.js.JsFile;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class DirectoriesAndFiles extends JavaScriptObject {
	protected DirectoriesAndFiles() {
	}

	public final native JsArray<JsDirectory> getDirectories() /*-{
		return this.directories;
	}-*/;

	public final native JsArray<JsFile> getFiles() /*-{
		return this.files;
	}-*/;

	public static DirectoriesAndFiles create(JsArray<JsDirectory> directories, JsArray<JsFile> files) {
		DirectoriesAndFiles result = DirectoriesAndFiles.createObject().cast();
		result.putValues(directories, files);
		return result;
	}

	private final native void putValues(JsArray<JsDirectory> directories,
			JsArray<JsFile> files) /*-{
		this.directories = directories;
		this.files = files;
	}-*/;
}
