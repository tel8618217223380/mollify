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

import org.sjarvela.mollify.client.ResourceId;

public enum FileSystemAction implements ResourceId {
	download, rename, copy, move, delete, upload, details, create_folder, download_as_zip, set_description, remove_description, get_item_permissions, view;

	public boolean isApplicableToDirectory() {
		return (this.equals(upload) || this.equals(create_folder)
				|| this.equals(move) || this.equals(rename)
				|| this.equals(delete) || this.equals(download_as_zip)
				|| this.equals(set_description) || this
				.equals(remove_description));
	}

	public boolean isApplicableToFile() {
		return !(this.equals(upload) || this.equals(create_folder));
	}

	public boolean isApplicable(FileSystemItem item) {
		if (item.isFile())
			return isApplicableToFile();
		return isApplicableToDirectory();
	}
}
