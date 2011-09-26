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

import java.util.Date;

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.util.DateTime;

import com.google.gwt.i18n.client.DateTimeFormat;

public class FileDetails extends ItemDetails {
	protected FileDetails() {
	}

	public final Date getLastAccessed() {
		String string = getLastAccessedString();
		if (string == null)
			return null;
		return DateTime.getInstance().getInternalFormat().parse(string);
	}

	public final Date getLastChanged() {
		String string = getLastChangedString();
		if (string == null)
			return null;
		return DateTime.getInstance().getInternalFormat().parse(string);
	}

	public final Date getLastModified() {
		String string = getLastModifiedString();
		if (string == null)
			return null;
		return DateTime.getInstance().getInternalFormat().parse(string);
	}

	public final native JsObj getFileViewerEditor() /*-{
		return this.fileviewereditor;
	}-*/;

	private final native String getLastAccessedString() /*-{
		return this.last_accessed;
	}-*/;

	private final native String getLastChangedString() /*-{
		return this.last_changed;
	}-*/;

	private final native String getLastModifiedString() /*-{
		return this.last_modified;
	}-*/;

	public static FileDetails create(Date lastAccessed, Date lastChanged,
			Date lastModified, String description, FilePermission permission,
			String preview, JsObj view) {
		FileDetails result = FileDetails.createObject().cast();
		DateTimeFormat fmt = DateTime.getInstance().getInternalFormat();
		result.putData(fmt.format(lastAccessed), fmt.format(lastChanged),
				fmt.format(lastModified), description,
				permission.getStringValue(), preview, view);
		return result;
	}

	private final native void putData(String lastAccessed, String lastChanged,
			String lastModified, String description, String permission,
			String preview, JsObj view) /*-{
		this.last_accessed = lastAccessed;
		this.last_changed = lastChanged;
		this.last_modified = lastModified;
		this.description = description;
		this.permission = permission;
		this.preview = preview;
		this.view = view;
	}-*/;
}
