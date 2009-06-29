/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common;

import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.Grid;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.GridDataProvider;

public class UserList extends Grid<User> implements GridDataProvider<User> {
	public static GridColumn COLUMN_NAME;
	public static GridColumn COLUMN_TYPE;
	public static List<GridColumn> ALL_COLUMNS = null;

	private final TextProvider textProvider;

	public UserList(TextProvider textProvider, String style) {
		super(StyleConstants.USER_LIST_HEADER, getColumns(textProvider));
		this.textProvider = textProvider;

		this.setStylePrimaryName(StyleConstants.USER_LIST);
		if (style != null)
			this.addStyleDependentName(style);
		this.setDataProvider(this);
	}

	private static List<GridColumn> getColumns(TextProvider textProvider) {
		if (ALL_COLUMNS == null) {
			COLUMN_NAME = new DefaultGridColumn("name", textProvider
					.getStrings().userListColumnTitleName(), false);
			COLUMN_TYPE = new DefaultGridColumn("type", textProvider
					.getStrings().userListColumnTitleType(), false);

			ALL_COLUMNS = Arrays.asList((GridColumn) COLUMN_NAME,
					(GridColumn) COLUMN_TYPE);
		}

		return ALL_COLUMNS;
	}

	public String getColumnStyle(GridColumn column) {
		return StyleConstants.USER_LIST_COLUMN_PREFIX + column.getId();
	}

	public GridData getData(User user, GridColumn column) {
		if (column.equals(UserList.COLUMN_NAME))
			return new GridData.Text(user.getName());
		else if (column.equals(UserList.COLUMN_TYPE))
			return new GridData.Text(user.getType().getLocalizedText(
					textProvider));
		return new GridData.Text("");
	}

	private static final List<String> ROW_STYLE = Arrays
			.asList(StyleConstants.USER_LIST_ROW);

	public List<String> getRowStyles(User t) {
		return ROW_STYLE;
	}
}
