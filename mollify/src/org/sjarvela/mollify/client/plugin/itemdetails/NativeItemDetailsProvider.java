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
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemDetailsProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemDetailsSection;

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
		return convert(invokeNativeProvider(item.asJs()));
	}

	private ItemDetails convert(JavaScriptObject result) {
		List<ItemDetailsSection> sections = new ArrayList();

		JsObj r = result.cast();
		JsArray<JsObj> s = r.getArray("sections");
		for (int i = 0; i < s.length(); i++) {
			JsObj section = s.get(i);
			String title = section.getString("title");
			JavaScriptObject callback = section.getObject("open");

			sections.add(new NativeItemDetailSection(title, callback));
		}
		return new ItemDetails(sections);
	}

	private final native JavaScriptObject invokeNativeProvider(
			JavaScriptObject item) /*-{
		cb = this.@org.sjarvela.mollify.client.plugin.itemdetails.NativeItemDetailsProvider::dp;
		return cb(item);
	}-*/;

}
