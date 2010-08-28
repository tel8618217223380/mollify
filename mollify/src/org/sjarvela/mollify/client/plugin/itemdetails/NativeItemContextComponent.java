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
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextComponent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class NativeItemContextComponent implements ItemContextComponent {
	static int index = 0;

	private final String html;

	@SuppressWarnings("unused")
	private final JavaScriptObject onInit;
	@SuppressWarnings("unused")
	private final JavaScriptObject onContextClose;

	private Widget component = null;

	public NativeItemContextComponent(JavaScriptObject init,
			JavaScriptObject contextClose, String html) {
		this.onInit = init;
		this.onContextClose = contextClose;
		this.html = html;
	}

	@Override
	public Widget getComponent() {
		if (component == null)
			component = createComponent();
		return component;
	}

	private Widget createComponent() {
		FlowPanel p = new FlowPanel();
		p.setStyleName(StyleConstants.ITEM_CONTEXT_COMPONENT);
		p.getElement().setId("item-component-" + index++);
		p.getElement().setInnerHTML(html);
		return p;
	}

	@Override
	public boolean onInit(FileSystemItem item, ItemDetails details) {
		return invokeInit(component.getElement().getId(), item.asJs(), details);
	}

	@Override
	public void onContextClose() {
		invokeContextClose();
	}

	private final native boolean invokeInit(String elementId,
			JavaScriptObject item, JavaScriptObject details) /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemContextComponent::onInit;
		if (!cb) return;
		var ret = cb(elementId, item, details);
		return ret == true;
	}-*/;

	private final native JavaScriptObject invokeContextClose() /*-{
		var cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemContextComponent::onContextClose;
		if (!cb) return;
		cb();
	}-*/;
}
