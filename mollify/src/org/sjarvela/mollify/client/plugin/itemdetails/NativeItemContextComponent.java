/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin.itemdetails;

import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextComponent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

public class NativeItemContextComponent implements ItemContextComponent {
	protected final FileSystemItem item;
	private final String html;

	@SuppressWarnings("unused")
	private final JavaScriptObject onInit;
	@SuppressWarnings("unused")
	private final JavaScriptObject onDispose;

	public NativeItemContextComponent(FileSystemItem item,
			JavaScriptObject init, JavaScriptObject dispose, String html) {
		this.item = item;
		this.onInit = init;
		this.onDispose = dispose;
		this.html = html;
	}

	@Override
	public String getHtml() {
		return html == null ? "" : html;
	}

	@Override
	public void onInit(Widget content, FileDetails details) {
		invokeInit(content.getElement().getId(), item.asJs(), details);
	}

	@Override
	public void onDispose() {
		invokeDispose();
	}

	private final native JavaScriptObject invokeInit(String elementId,
			JavaScriptObject item, JavaScriptObject details) /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemContextComponent::onInit;
		if (!cb) return;
		cb(elementId, item, details);
	}-*/;

	private final native JavaScriptObject invokeDispose() /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemContextComponent::onDispose;
		if (!cb) return;
		cb();
	}-*/;
}
