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

import com.google.gwt.user.client.ui.Label;

public class GridColumnSortButton extends Label {
	private Sort sort = Sort.none;
	private final String style;

	public GridColumnSortButton(GridColumn column, String styleClass) {
		this.style = styleClass;
		this.getElement().setId(styleClass + "-" + column.getId());
		updateStyle();
	}

	public void setSort(Sort sort) {
		this.sort = sort;
		updateStyle();
	}

	private void updateStyle() {
		this.setStyleName(style + "-" + sort.name());
	}
}
