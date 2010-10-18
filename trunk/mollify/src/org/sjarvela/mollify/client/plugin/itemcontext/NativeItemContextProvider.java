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

	public NativeItemContextProvider(JavaScriptObject dp) {
		this.dp = dp;
	}

	@Override
	public ItemContext getItemContext(FileSystemItem item, ItemDetails details) {
		return convert(item, invokeNativeProvider(item.asJs()));
	}

	private ItemContext convert(FileSystemItem item, JavaScriptObject result) {
		JsObj r = result.cast();
		return new ItemContext(createComponents(r), createActions(r));
	}

	private List<ItemContextComponent> createComponents(JsObj r) {
		List<ItemContextComponent> components = new ArrayList();
		JsArray<JsObj> componentList = r.getArray("components");

		for (int i = 0; i < componentList.length(); i++) {
			JsObj c = componentList.get(i);
			String type = c.getString("type").trim().toLowerCase();

			if ("section".equals(type))
				components.add(new NativeItemContextSection(c
						.getString("title"), c.getString("html"), c
						.getObject("on_init"), c.getObject("on_context_close"),
						c.getObject("on_open"), c.getObject("on_close")));
			else if ("custom".equals(type))
				components.add(new NativeItemContextComponent(c
						.getObject("on_init"), c.getObject("on_context_close"),
						c.getString("html")));
			else
				throw new RuntimeException("Invalid component type: " + type);
		}
		return components;
	}

	private Map<ActionType, List<ContextActionItem>> createActions(JsObj r) {
		Map<ActionType, List<ContextActionItem>> actions = new HashMap();
		JsObj actionsDef = r.getJsObj("actions");

		addActions(actions, ActionType.Primary, actionsDef.getArray("primary"));
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

	private final native JavaScriptObject invokeNativeProvider(
			JavaScriptObject item) /*-{
		cb = this.@org.sjarvela.mollify.client.plugin.itemcontext.NativeItemContextProvider::dp;
		return cb(item);
	}-*/;

}
