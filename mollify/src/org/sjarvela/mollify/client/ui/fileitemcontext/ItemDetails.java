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
import java.util.Collections;
import java.util.List;

public class ItemDetails {
	public static ItemDetails Empty = null;
	private final List<ItemDetailsComponent> components;

	static {
		Empty = new ItemDetails(Collections.EMPTY_LIST);
	}

	public ItemDetails(List<ItemDetailsComponent> components) {
		this.components = new ArrayList(components);
	}

	public List<ItemDetailsComponent> getComponents() {
		return components;
	}

	public ItemDetails merge(ItemDetails other) {
		List<ItemDetailsComponent> newComponents = new ArrayList(components);
		newComponents.addAll(other.getComponents());
		return new ItemDetails(newComponents);
	}
}
