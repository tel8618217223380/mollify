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
	private SortOrder sort = SortOrder.none;

	public GridColumnSortButton(GridColumn column, String styleClass) {
		this.setStylePrimaryName(styleClass);
		this.getElement().setId(styleClass + "-" + column.getId());
		this.addStyleDependentName(sort.name());
	}

	public void setSort(SortOrder sort) {
		this.removeStyleDependentName(this.sort.name());
		this.sort = sort;
		this.addStyleDependentName(sort.name());
	}
}
