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
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemDetailsComponent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

public class NativeItemDetailComponent implements ItemDetailsComponent {
	private final FileSystemItem item;
	private final String html;

	@SuppressWarnings("unused")
	private final JavaScriptObject onInit;


	public NativeItemDetailComponent(FileSystemItem item, JavaScriptObject init, String html) {
		this.item = item;
		this.onInit = init;
		this.html = html;
	}

	@Override
	public String getHtml() {
		return html;
	}

	@Override
	public void onInit(Widget content) {
		invokeInit(content.getElement().getId(), item.asJs());
	}

	private final native JavaScriptObject invokeInit(String elementId,
			JavaScriptObject item) /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemDetailComponent::onInit;
		if (!cb) return;
		cb(elementId, item);
	}-*/;
}
