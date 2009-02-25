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

public class File extends FileSystemItem {
	public static File Empty = new File();
	private final String extension;
	private final int size;

	private File() {
		super("", "");
		extension = "";
		size = 0;
	}

	protected File(JsFile file) {
		this(file.getId(), file.getName(), file.getExtension(), file.getSize());
	}

	public File(String id, String name, String extension, int size) {
		super(id, name);
		this.extension = extension;
		this.size = size;
	}

	public final String getExtension() {
		return extension;
	}

	public final int getSize() {
		return size;
	}

	public final int getSizeInKB() {
		return getSize() / 1024;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return this == Empty;
	}

}
