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

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.js.JsDirectory;
import org.sjarvela.mollify.client.filesystem.js.JsFile;

import com.google.gwt.core.client.JsArray;

public abstract class FileSystemItem {
	String id;
	String name;

	public static File createFrom(JsFile file) {
		return new File(file);
	}

	public static Directory createFrom(JsDirectory dir) {
		return new Directory(dir);
	}

	public static List<Directory> createFromDirectories(
			JsArray<JsDirectory> directories) {
		List<Directory> result = new ArrayList();
		for (int i = 0; i < directories.length(); i++)
			result.add(createFrom(directories.get(i)));
		return result;
	}

	public static List<File> createFromFiles(JsArray<JsFile> files) {
		List<File> result = new ArrayList();
		for (int i = 0; i < files.length(); i++)
			result.add(createFrom(files.get(i)));
		return result;
	}
	
	protected FileSystemItem(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public abstract boolean isFile();
	
	public abstract boolean isEmpty();



}
