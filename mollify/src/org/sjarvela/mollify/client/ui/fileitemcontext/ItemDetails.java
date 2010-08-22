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
	private final List<ItemDetailsSection> sections;

	static {
		Empty = new ItemDetails(Collections.EMPTY_LIST);
	}

	public ItemDetails(List<ItemDetailsSection> sections) {
		this.sections = new ArrayList(sections);
	}

	public List<ItemDetailsSection> getSections() {
		return sections;
	}

	public ItemDetails merge(ItemDetails other) {
		List<ItemDetailsSection> newSections = new ArrayList(sections);
		newSections.addAll(other.getSections());
		return new ItemDetails(newSections);
	}
}
