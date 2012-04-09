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

import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeFileListComparator implements GridComparator<JsFilesystemItem> {
	private final SortOrder sort;
	private final NativeColumnSpec colSpec;
	private final JsObj data;

	public NativeFileListComparator(NativeColumnSpec colSpec, SortOrder sort,
			JsObj data) {
		this.colSpec = colSpec;
		this.sort = sort;
		this.data = data;
	}

	@Override
	public int compare(JsFilesystemItem i1, JsFilesystemItem i2) {
		return invokeSortCallback(colSpec.getSortCallback(), i1, i2,
				sort.getCompareFactor(), data);
	}

	protected static native final int invokeSortCallback(JavaScriptObject cb,
			JavaScriptObject i1, JavaScriptObject i2, int f,
			JavaScriptObject data) /*-{
		return cb(i1, i2, f, data);
	}-*/;

	@Override
	public String getColumnId() {
		return colSpec.getId();
	}

	@Override
	public SortOrder getSort() {
		return sort;
	}

}
