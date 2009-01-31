/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.FileSystemItem;

public enum FileSystemAction {
	DOWNLOAD, RENAME, DELETE, UPLOAD, DETAILS, CREATE_FOLDER;

	public boolean isApplicableToDirectory() {
		return (this.equals(UPLOAD) || this.equals(CREATE_FOLDER) || this
				.equals(RENAME));
	}

	public boolean isApplicableToFile() {
		return !(this.equals(UPLOAD) || this.equals(CREATE_FOLDER));
	}

	public boolean isApplicable(FileSystemItem item) {
		if (item.isFile())
			return isApplicableToFile();
		return isApplicableToDirectory();
	}
}
