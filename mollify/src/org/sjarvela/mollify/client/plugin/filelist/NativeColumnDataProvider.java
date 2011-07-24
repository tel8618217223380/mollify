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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.common.grid.GridData;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeColumnDataProvider {
	private final NativeGridColumn column;

	public NativeColumnDataProvider(NativeGridColumn column) {
		this.column = column;
	}

	public GridData getData(FileSystemItem item, JsObj data) {
		String html = invokeContentCallback(column.getColSpec()
				.getContentCallback(), item.asJs());
		return new GridData.HTML(html);
	}

	protected static native final String invokeContentCallback(
			JavaScriptObject cb, JavaScriptObject i) /*-{
		if (!cb)
			return "";
		return cb(i);
	}-*/;
}
