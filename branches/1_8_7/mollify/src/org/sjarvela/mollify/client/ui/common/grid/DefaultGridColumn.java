/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common.grid;

public class DefaultGridColumn implements GridColumn {
	private final boolean sortable;
	private final String title;
	private final String id;

	public DefaultGridColumn(String id, String title, boolean sortable) {
		this.id = id;
		this.title = title;
		this.sortable = sortable;
	}

	public boolean isSortable() {
		return sortable;
	}

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}
}