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

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemDetails;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemDetailsComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemDetailsProvider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class NativeItemDetailsProvider implements ItemDetailsProvider {
	@SuppressWarnings("unused")
	private final JavaScriptObject dp;

	public NativeItemDetailsProvider(JavaScriptObject dp) {
		this.dp = dp;
	}

	@Override
	public ItemDetails getItemDetails(FileSystemItem item) {
		return convert(item, invokeNativeProvider(item.asJs()));
	}

	private ItemDetails convert(FileSystemItem item, JavaScriptObject result) {
		List<ItemDetailsComponent> sections = new ArrayList();

		JsObj r = result.cast();
		JsArray<JsObj> components = r.getArray("components");
		for (int i = 0; i < components.length(); i++) {
			JsObj c = components.get(i);
			String type = c.getString("type").trim().toLowerCase();

			if ("section".equals(type))
				sections.add(new NativeItemDetailSection(item, c
						.getString("title"), c.getString("html"), c
						.getObject("onInit"), c.getObject("onOpen"), c
						.getObject("onClose")));
			else if ("custom".equals(type))
				sections.add(new NativeItemDetailComponent(item, c
						.getObject("onInit"), c.getString("html")));
			else
				throw new RuntimeException("Invalid component type: " + type);
		}
		return new ItemDetails(sections);
	}

	private final native JavaScriptObject invokeNativeProvider(
			JavaScriptObject item) /*-{
		cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemDetailsProvider::dp;
		return cb(item);
	}-*/;

}
