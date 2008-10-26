/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;

import com.google.gwt.core.client.JsArray;

public class FileViewModel {
	private JsArray<Directory> rootDirectories;

	private JsArray<Directory> directories;
	private JsArray<File> files;

	private DirectoryModel directoryModel;

	public FileViewModel() {
		super();
		clear();
	}

	void clear() {
		rootDirectories = JsArray.createArray().cast();
		directories = JsArray.createArray().cast();
		files = JsArray.createArray().cast();
		directoryModel = new DirectoryModel();
	}

	public void setData(JsArray<Directory> directories, JsArray<File> files) {
		this.directories = directories;
		this.files = files;
	}

	public JsArray<Directory> getDirectories() {
		return directories;
	}

	public void setDirectories(JsArray<Directory> directories) {
		this.directories = directories;
	}

	public JsArray<File> getFiles() {
		return files;
	}

	public void setFiles(JsArray<File> files) {
		this.files = files;
	}

	public JsArray<Directory> getRootDirectories() {
		return rootDirectories;
	}

	public void setRootDirectories(JsArray<Directory> dirs) {
		this.rootDirectories = dirs;
	}

	public DirectoryModel getDirectoryModel() {
		return directoryModel;
	}
}
