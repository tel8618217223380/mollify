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

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.FileView;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class NativeFileView {
	private final FileView fileView;

	public NativeFileView(FileView fileView) {
		this.fileView = fileView;
	}

	public void refresh() {
		fileView.refreshCurrentFolder();
	}

	public JavaScriptObject getCurrentFolder() {
		return fileView.getCurrentFolder().asJs();
	}

	public void openUploader(boolean forceBasic) {
		fileView.openUploader(forceBasic);
	}

	public JsArray getItems() {
		List<FileSystemItem> items = fileView.getAllItems();
		List<JavaScriptObject> jsItems = new ArrayList();
		for (FileSystemItem item : items)
			jsItems.add(item.asJs());
		return JsUtil.asJsArray(jsItems, JavaScriptObject.class);
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	private native JavaScriptObject createJs(NativeFileView fs) /*-{
		var o = {};

		o.refresh = function() {
			return fs.@org.sjarvela.mollify.client.plugin.NativeFileView::refresh()();
		}

		o.items = function() {
			return fs.@org.sjarvela.mollify.client.plugin.NativeFileView::getItems()();
		}

		o.currentFolder = function() {
			return fs.@org.sjarvela.mollify.client.plugin.NativeFileView::getCurrentFolder()();
		}

		o.openBasicUploader = function(b) {
			var fb = false;
			if (b && b == true)
				fb = true;
			fs.@org.sjarvela.mollify.client.plugin.NativeFileView::openUploader(Z)(fb);
		}

		return o;
	}-*/;
}
