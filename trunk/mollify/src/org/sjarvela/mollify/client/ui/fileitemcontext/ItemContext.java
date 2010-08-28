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

public class ItemContext {
	public static ItemContext Empty = null;
	private final List<ItemContextComponent> components;

	static {
		Empty = new ItemContext(Collections.EMPTY_LIST);
	}

	public ItemContext(List<ItemContextComponent> components) {
		this.components = new ArrayList(components);
	}

	public List<ItemContextComponent> getComponents() {
		return components;
	}

	public ItemContext add(ItemContext other) {
		List<ItemContextComponent> newComponents = new ArrayList(components);
		newComponents.addAll(other.getComponents());
		return new ItemContext(newComponents);
	}
}
