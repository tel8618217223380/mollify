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

import org.sjarvela.mollify.client.ui.filelist.ColumnSpec;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeColumnSpec implements ColumnSpec {
	private final String id;
	private final JavaScriptObject contentCb;
	private final JavaScriptObject sortCb;

	public NativeColumnSpec(String id, JavaScriptObject contentCb,
			JavaScriptObject sortCb) {
		this.id = id;
		this.contentCb = contentCb;
		this.sortCb = sortCb;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isSortable() {
		return sortCb != null;
	}

	public JavaScriptObject getSortCallback() {
		return sortCb;
	}
}
