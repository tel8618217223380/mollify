/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin.itemcontext;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextCallbackAction;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeItemContextAction extends ContextCallbackAction {
	private final JavaScriptObject cb;

	public NativeItemContextAction(String title, JavaScriptObject cb) {
		super(title);
		this.cb = cb;
	}

	private final native JavaScriptObject invokeNativeCallback(
			JavaScriptObject item) /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemcontext.NativeItemContextAction::cb;
		if (!cb) return;
		cb(item);
	}-*/;

	@Override
	public void onContextAction(FileSystemItem item) {
		invokeNativeCallback(item.asJs());
	}

}
