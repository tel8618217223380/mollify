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

import org.sjarvela.mollify.client.filesystem.js.JsFile;

import com.google.gwt.core.client.JavaScriptObject;

public class File extends FileSystemItem {
	public static File Empty = new File();

	private final String extension;
	private final Long size;

	private File() {
		super("", "", "", "", "");
		extension = "";
		size = 0l;
	}

	protected File(JsFile file) {
		this(file.getId(), file.getRootId(), file.getName(), file.getPath(),
				file.getParentId(), file.getExtension(), file.getSize());
	}

	public File(String id, String rootId, String name, String path,
			String parentId, String extension, long size) {
		super(id, rootId, name, path, parentId);
		this.extension = extension;
		this.size = size;
	}

	public final String getExtension() {
		return extension;
	}

	public final long getSize() {
		return size;
	}

	public final long getSizeInKB() {
		return getSize() / 1024l;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return this == Empty;
	}

	@Override
	public JavaScriptObject asJs() {
		return JsFile.create(this);
	}
}
