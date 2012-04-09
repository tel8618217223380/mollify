/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext;

/*import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;

public class ItemContext {
	public enum ActionType {
		Download, Primary, Secondary
	}

	private final List<ItemContextComponent> components;
	private final Map<ActionType, List<ContextActionItem>> actions;

	public ItemContext(List<ItemContextComponent> components,
			Map<ActionType, List<ContextActionItem>> actions) {
		this.components = new ArrayList(components);
		this.actions = actions;
	}

	public ItemContext() {
		this.actions = new HashMap();
		this.components = new ArrayList();
	}

	public List<ItemContextComponent> getComponents() {
		return components;
	}

	public Map<ActionType, List<ContextActionItem>> getActions() {
		return actions;
	}

	public ItemContext add(ItemContext other, boolean comp) {
		List<ItemContextComponent> newComponents = new ArrayList(components);
		if (comp) {
			newComponents.addAll(other.getComponents());
			Collections.sort(newComponents,
					new Comparator<ItemContextComponent>() {
						@Override
						public int compare(ItemContextComponent a,
								ItemContextComponent b) {
							return a.getIndex().compareTo(b.getIndex());
						}
					});
		}

		Map<ActionType, List<ContextActionItem>> newActions = new HashMap(
				actions);
		for (Entry<ActionType, List<ContextActionItem>> e : other.actions
				.entrySet()) {
			List<ContextActionItem> items = newActions.get(e.getKey());
			if (items == null) {
				items = new ArrayList();
				newActions.put(e.getKey(), items);
			}
			items.addAll(e.getValue());
		}

		return new ItemContext(newComponents, newActions);
	}

	public static ItemContextBuilder def() {
		return new ItemContextBuilder();
	}

	public static class ItemContextBuilder {
		ItemContextComponentsBuilder components = new ItemContextComponentsBuilder();
		ItemContextActionsBuilder actions = new ItemContextActionsBuilder();

		public ItemContextComponentsBuilder components() {
			return components;
		}

		public ItemContextActionsBuilder actions() {
			return actions;
		}

		public ItemContext create() {
			return new ItemContext(components.list, actions.getMap());
		}
	}

	public static class ItemContextComponentsBuilder {
		private final List<ItemContextComponent> list = new ArrayList();

		public void add(ItemContextComponent c) {
			list.add(c);
		}
	}

	public static class ItemContextActionsBuilder {
		private final Map<ActionType, ItemContextActionTypeBuilder> actions = new HashMap();

		private Map<ActionType, List<ContextActionItem>> getMap() {
			Map<ActionType, List<ContextActionItem>> result = new HashMap();
			for (Entry<ActionType, ItemContextActionTypeBuilder> e : actions
					.entrySet())
				result.put(e.getKey(), e.getValue().list);
			return result;
		}

		public ItemContextActionTypeBuilder type(ActionType type) {
			ItemContextActionTypeBuilder builder = actions.get(type);
			if (builder == null) {
				builder = new ItemContextActionTypeBuilder();
				actions.put(type, builder);
			}
			return builder;
		}
	}

	public static class ItemContextActionTypeBuilder {
		private final List<ContextActionItem> list = new ArrayList();

		public void add(ContextActionItem a) {
			list.add(a);
		}

		public void add(ResourceId action, String title) {
			list.add(new ContextAction(action, title));
		}

		public void addSeparator() {
			list.add(new ContextActionSeparator());
		}
	}
}*/
