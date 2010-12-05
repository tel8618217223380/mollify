/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin;

import org.sjarvela.mollify.client.FileView;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeFileView {
	private final FileView fileView;

	public NativeFileView(FileView fileView) {
		this.fileView = fileView;
	}

	public void refresh() {
		fileView.refreshCurrentFolder();
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	private native JavaScriptObject createJs(NativeFileView fs) /*-{
		var o = {};

		o.refresh = function() {
			return fs.@org.sjarvela.mollify.client.plugin.NativeFileView::refresh()();
		}

		return o;
	}-*/;
}
