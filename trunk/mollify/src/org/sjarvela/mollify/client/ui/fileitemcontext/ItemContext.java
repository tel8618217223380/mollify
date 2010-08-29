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

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;

public class ItemContext {
	private final List<ItemContextComponent> components;
	private final List<MenuItem> downloadItems;
	private final List<MenuItem> actionItems;

	public ItemContext(List<ItemContextComponent> components,
			List<MenuItem> downloadItems, List<MenuItem> actionItems) {
		this.downloadItems = downloadItems;
		this.actionItems = actionItems;
		this.components = new ArrayList(components);
	}

	public List<ItemContextComponent> getComponents() {
		return components;
	}

	public List<MenuItem> getDownloadItems() {
		return downloadItems;
	}

	public List<MenuItem> getActionItems() {
		return actionItems;
	}

	public ItemContext add(ItemContext other) {
		List<ItemContextComponent> newComponents = new ArrayList(components);
		newComponents.addAll(other.getComponents());

		List<MenuItem> newDownloadItems = new ArrayList(downloadItems);
		newDownloadItems.addAll(other.getDownloadItems());

		List<MenuItem> newActionItems = new ArrayList(actionItems);
		newActionItems.addAll(other.getActionItems());

		return new ItemContext(newComponents, newDownloadItems, newActionItems);
	}
}
