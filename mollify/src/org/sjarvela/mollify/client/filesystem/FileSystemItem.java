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
	protected final String id;
	protected final String name;
	protected final String parentId;

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

	protected FileSystemItem(String id, String name, String parentId) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public final String getParentId() {
		return parentId;
	}
	
	public abstract boolean isFile();

	public abstract boolean isEmpty();

	@Override
	public int hashCode() {
		return new Boolean(isFile()).hashCode() + id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof FileSystemItem))
			return false;

		FileSystemItem other = (FileSystemItem) obj;
		return isFile() == other.isFile() && id.equals(other.id);
	}
}
