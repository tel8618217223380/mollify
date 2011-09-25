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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextActionItem;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextActionSeparator;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext.ActionType;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class NativeItemContextProvider implements ItemContextProvider {
	private final JavaScriptObject dp;
	private final JavaScriptObject rq;

	public NativeItemContextProvider(JavaScriptObject dp, JavaScriptObject rq) {
		this.dp = dp;
		this.rq = rq;
	}

	@Override
	public JavaScriptObject getItemContextRequestData(FileSystemItem item) {
		if (rq == null)
			return null;
		return invokeNativeRequestDataCallback(item.asJs());
	}

	@Override
	public ItemContext getItemContext(FileSystemItem item, ItemDetails details) {
		return convert(item, invokeNativeProvider(item.asJs(), details));
	}

	private ItemContext convert(FileSystemItem item, JavaScriptObject result) {
		JsObj r = result.cast();
		return new ItemContext(createComponents(r), createActions(r));
	}

	private List<ItemContextComponent> createComponents(JsObj r) {
		List<ItemContextComponent> components = new ArrayList();
		if (r == null || !r.hasValue("components"))
			return components;

		JsArray<JsObj> componentList = r.getArray("components");
		for (int i = 0; i < componentList.length(); i++) {
			JsObj c = componentList.get(i);
			String type = c.getString("type").trim().toLowerCase();
			Integer index = c.hasValue("index") ? c.getInt("index") : 1000 + i;

			if ("section".equals(type))
				components.add(new NativeItemContextSection(c
						.getString("title"), c.getString("html"), c
						.getObject("on_init"), c.getObject("on_request"), c
						.getObject("on_context_close"), c.getObject("on_open"),
						c.getObject("on_close"), index));
			else if ("custom".equals(type))
				components.add(new NativeItemContextComponent(c
						.getObject("on_init"), c.getObject("on_request"), c
						.getObject("on_context_close"), c.getString("html"),
						index));
			else
				throw new RuntimeException("Invalid component type: " + type);
		}
		return components;
	}

	private Map<ActionType, List<ContextActionItem>> createActions(JsObj r) {
		Map<ActionType, List<ContextActionItem>> actions = new HashMap();
		if (r == null || !r.hasValue("actions"))
			return actions;

		JsObj actionsDef = r.getJsObj("actions");

		if (actionsDef.hasValue("primary"))
			addActions(actions, ActionType.Primary,
					actionsDef.getArray("primary"));

		if (actionsDef.hasValue("secondary"))
			addActions(actions, ActionType.Secondary,
					actionsDef.getArray("secondary"));

		return actions;
	}

	private void addActions(Map<ActionType, List<ContextActionItem>> actions,
			ActionType type, JsArray<JsObj> actionList) {
		List<ContextActionItem> list = new ArrayList();
		for (int i = 0; i < actionList.length(); i++) {
			JsObj c = actionList.get(i);

			String title = c.getString("title");
			if ("-".equals(title))
				list.add(new ContextActionSeparator());
			else
				list.add(new NativeItemContextAction(title, c
						.getObject("callback")));
		}
		if (!list.isEmpty())
			actions.put(type, list);
	}

	private final native JavaScriptObject invokeNativeRequestDataCallback(
			JavaScriptObject item) /*-{
		cb = this.@org.sjarvela.mollify.client.plugin.itemcontext.NativeItemContextProvider::rq;
		return cb(item);
	}-*/;

	private final native JavaScriptObject invokeNativeProvider(
			JavaScriptObject item, JavaScriptObject d) /*-{
		cb = this.@org.sjarvela.mollify.client.plugin.itemcontext.NativeItemContextProvider::dp;
		return cb(item, d);
	}-*/;

}
