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
import org.sjarvela.mollify.client.ui.common.grid.GridData;

public class NativeColumnDataProvider {
	private final NativeGridColumn column;

	public NativeColumnDataProvider(NativeGridColumn column) {
		this.column = column;
	}

	public GridData getData(FileSystemItem item) {
		// return invokeSortCallback(colSpec.getSortCallback(), i1.asJs(),
		// i2.asJs(), sort.getCompareFactor());
		return new GridData.Text("foo");
	}
	// protected static native final int invokeSortCallback(JavaScriptObject cb,
	// JavaScriptObject i1, JavaScriptObject i2, int f) /*-{
	// return cb(i1, i2, f);
	// }-*/;
}
