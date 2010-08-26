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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextSection;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeItemContextSection extends NativeItemContextComponent
		implements ItemContextSection {
	private final String title;

	@SuppressWarnings("unused")
	private final JavaScriptObject onOpen;
	@SuppressWarnings("unused")
	private final JavaScriptObject onClose;

	public NativeItemContextSection(FileSystemItem item, String title,
			String html, JavaScriptObject init, JavaScriptObject dispose,
			JavaScriptObject open, JavaScriptObject close) {
		super(item, init, dispose, html);
		this.title = title;
		onOpen = open;
		onClose = close;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void onOpen() {
		invokeOpen();
	}

	@Override
	public void onClose() {
		invokeClose();
	}

	private final native JavaScriptObject invokeOpen() /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemContextSection::onOpen;
		if (!cb) return;
		cb();
	}-*/;

	private final native JavaScriptObject invokeClose() /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemContextSection::onClose;
		if (!cb) return;
		cb();
	}-*/;

}
