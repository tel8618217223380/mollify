/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin.filelist;

/*import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeGridColumn implements GridColumn {

	private final String id;
	private final NativeColumnSpec colSpec;
	private final String title;
	private final boolean sortable;

	public NativeGridColumn(String id, NativeColumnSpec colSpec, String title,
			boolean sortable) {
		this.id = id;
		this.colSpec = colSpec;
		this.title = title;
		this.sortable = sortable;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isSortable() {
		return sortable;
	}

	public NativeColumnSpec getColSpec() {
		return colSpec;
	}

	public GridData getData(FileSystemItem item, JsObj data) {
		String html = invokeContentCallback(colSpec.getContentCallback(),
				item.asJs(), data);
		return new GridData.HTML(html);
	}

	protected static native final String invokeContentCallback(
			JavaScriptObject cb, JavaScriptObject i, JavaScriptObject data) /*-{
		if (!cb)
			return "";
		return cb(i, data);
	}-;
}*/
