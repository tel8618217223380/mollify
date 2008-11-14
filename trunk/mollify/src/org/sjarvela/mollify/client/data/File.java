/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.data;

public class File extends FileSystemItem {
	public static File Empty = new File();
	private final JsFile file;

	private File() {
		super("", "");
		file = null;
	}
	
	protected File(JsFile file) {
		super(file.getId(), file.getName());
		this.file = file;
	}

	public final String getExtension() {
		return file.getExtension();
	}

	public final int getSize() {
		return file.getSize();
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
