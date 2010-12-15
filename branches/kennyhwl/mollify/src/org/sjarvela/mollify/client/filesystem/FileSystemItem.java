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

import org.sjarvela.mollify.client.filesystem.js.JsFile;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public abstract class FileSystemItem {
	protected final String id;
	protected final String rootId;
	protected final String name;
	protected final String path;
	protected final String parentId;
	protected final boolean isProtected;

	public static File createFrom(JsFile file) {
		return new File(file);
	}

	public static Folder createFrom(JsFolder dir) {
		return new Folder(dir);
	}

	public static List<Folder> createFromFolders(JsArray<JsFolder> folders) {
		List<Folder> result = new ArrayList();
		for (int i = 0; i < folders.length(); i++) {
			JsFolder folder = folders.get(i);
			if (folder.getName() == null)
				continue;
			result.add(createFrom(folder));
		}
		return result;
	}

	public static List<File> createFromFiles(JsArray<JsFile> files) {
		List<File> result = new ArrayList();
		for (int i = 0; i < files.length(); i++) {
			JsFile file = files.get(i);
			if (file.getName() == null)
				continue;
			result.add(createFrom(file));
		}
		return result;
	}

	protected FileSystemItem(String id, String rootId, String name,
			String path, String parentId, boolean isProtected) {
		this.id = id;
		this.rootId = rootId;
		this.name = name;
		this.path = path;
		this.parentId = parentId;
		this.isProtected = isProtected;
	}

	public String getId() {
		return id;
	}

	public String getRootId() {
		return rootId;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public final String getParentId() {
		return parentId;
	}

	public boolean isProtected() {
		return isProtected;
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

	public String getParentPath() {
		return path.substring(0, path.length() - getName().length()
				- (isFile() ? 0 : 1));
	}

	public abstract JavaScriptObject asJs();
}
