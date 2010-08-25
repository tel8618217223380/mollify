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
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemDetailsSection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

public class NativeItemDetailSection implements ItemDetailsSection {
	private final FileSystemItem item;
	private final String title;
	private final String html;

	@SuppressWarnings("unused")
	private final JavaScriptObject onInit;
	@SuppressWarnings("unused")
	private final JavaScriptObject onOpen;
	@SuppressWarnings("unused")
	private final JavaScriptObject onClose;

	public NativeItemDetailSection(FileSystemItem item, String title,
			String html, JavaScriptObject init, JavaScriptObject open,
			JavaScriptObject close) {
		this.item = item;
		this.title = title;
		this.html = html;
		onInit = init;
		onOpen = open;
		onClose = close;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getHtml() {
		return html;
	}

	@Override
	public void onInit(Widget content) {
		invokeInit(content.getElement().getId(), item.asJs());
	}

	@Override
	public void onOpen(Widget content) {
		invokeOpen(content.getElement().getId(), item.asJs());
	}

	@Override
	public void onClose(Widget content) {
		invokeClose(content.getElement().getId(), item.asJs());
	}

	private final native JavaScriptObject invokeInit(String elementId,
			JavaScriptObject item) /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemDetailSection::onInit;
		if (!cb) return;
		cb(elementId, item);
	}-*/;

	private final native JavaScriptObject invokeOpen(String elementId,
			JavaScriptObject item) /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemDetailSection::onOpen;
		if (!cb) return;
		cb(elementId, item);
	}-*/;

	private final native JavaScriptObject invokeClose(String elementId,
			JavaScriptObject item) /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemDetailSection::onClose;
		if (!cb) return;
		cb(elementId, item);
	}-*/;

}
