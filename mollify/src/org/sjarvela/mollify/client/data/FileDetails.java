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

import java.util.Date;

import org.sjarvela.mollify.client.DateTime;

import com.google.gwt.core.client.JavaScriptObject;

public class FileDetails extends JavaScriptObject {
	protected FileDetails() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final Date getLastAccessed() {
		return DateTime.getInstance().getInternalFormat().parse(
				getLastAccessedString());
	}

	public final Date getLastChanged() {
		return DateTime.getInstance().getInternalFormat().parse(
				getLastChangedString());
	}

	public final Date getLastModified() {
		return DateTime.getInstance().getInternalFormat().parse(
				getLastModifiedString());
	}

	public final FilePermission getFilePermission() {
		return FilePermission.fromString(getFilePermissionString());
	}

	private final native String getLastAccessedString() /*-{
		return this.last_accessed;
	}-*/;

	private final native String getLastChangedString() /*-{
		return this.last_changed;
	}-*/;

	private final native String getLastModifiedString() /*-{
		return this.last_modified;
	}-*/;

	public final native String getDescription() /*-{
		return this.description;
	}-*/;

	private final native String getFilePermissionString() /*-{
		return this.permissions;
	}-*/;
}
