/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.js;

import org.sjarvela.mollify.client.filesystem.File;

import com.google.gwt.core.client.JavaScriptObject;

public class JsFile extends JavaScriptObject {
	protected JsFile() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getRootId() /*-{
		return this.root_id;
	}-*/;

	public final native String getParentId() /*-{
		return this.parent_id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getPath() /*-{
		return this.path;
	}-*/;

	public final native String getExtension() /*-{
		return this.extension;
	}-*/;

	public final Long getSize() {
		return Long.valueOf(getSizeString());
	}

	private final native String getSizeString() /*-{
		return this.size;
	}-*/;

	public static JsFile create(String id, String rootId, String name,
			String path, String parentId, String extension, long size) {
		JsFile result = JsFile.createObject().cast();
		result.putValues(id, rootId, name, path, parentId, extension,
				Long.toString(size));
		return result;
	}

	private final native void putValues(String id, String rootId, String name,
			String path, String parentId, String extension, String size) /*-{
		this.id = id;
		this.root_id = rootId;
		this.name = name;
		this.path = path;
		this.parent_id = parentId;
		this.extension = extension;
		this.size = size;
		this.is_file = true;
	}-*/;

	public static JavaScriptObject create(File file) {
		return create(file.getId(), file.getRootId(), file.getName(),
				file.getPath(), file.getParentId(), file.getExtension(),
				file.getSize());
	}

}
