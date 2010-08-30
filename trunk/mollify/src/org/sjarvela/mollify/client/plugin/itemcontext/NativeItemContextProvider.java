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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class NativeItemContextProvider implements ItemContextProvider {
	@SuppressWarnings("unused")
	private final JavaScriptObject dp;

	public NativeItemContextProvider(JavaScriptObject dp) {
		this.dp = dp;
	}

	@Override
	public ItemContext getItemContext(FileSystemItem item, ItemDetails details) {
		return convert(item, invokeNativeProvider(item.asJs()));
	}

	private ItemContext convert(FileSystemItem item, JavaScriptObject result) {
		List<ItemContextComponent> components = new ArrayList();

		JsObj r = result.cast();
		JsArray<JsObj> componentList = r.getArray("components");
		for (int i = 0; i < componentList.length(); i++) {
			JsObj c = componentList.get(i);
			String type = c.getString("type").trim().toLowerCase();

			if ("section".equals(type))
				components.add(new NativeItemContextSection(c
						.getString("title"), c.getString("html"), c
						.getObject("onInit"), c.getObject("onContextClose"), c
						.getObject("onOpen"), c.getObject("onClose")));
			else if ("custom".equals(type))
				components.add(new NativeItemContextComponent(c
						.getObject("onInit"), c.getObject("onContextClose"), c
						.getString("html")));
			else
				throw new RuntimeException("Invalid component type: " + type);
		}
		return new ItemContext(components, Collections.EMPTY_MAP);
	}

	private final native JavaScriptObject invokeNativeProvider(
			JavaScriptObject item) /*-{
		cb = this.@org.sjarvela.mollify.client.plugin.itemcontext.NativeItemContextProvider::dp;
		return cb(item);
	}-*/;

}
