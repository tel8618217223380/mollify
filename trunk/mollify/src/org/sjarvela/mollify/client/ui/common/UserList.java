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

/*import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.Grid;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.GridDataProvider;

public class UserList extends Grid<User> implements GridDataProvider<User> {
	public static String COLUMN_ID_NAME = "name";
	public static String COLUMN_ID_TYPE = "type";

	public UserList(TextProvider textProvider, String style) {
		super(textProvider, StyleConstants.USER_LIST_HEADER);

		this.setStylePrimaryName(StyleConstants.USER_LIST);
		if (style != null)
			this.addStyleDependentName(style);
		this.setDataProvider(this);
	}

	protected List<GridColumn> initColumns() {
		GridColumn columnName = new DefaultGridColumn(COLUMN_ID_NAME,
				textProvider.getText(Texts.userListColumnTitleName), false);
		GridColumn columnType = new DefaultGridColumn(COLUMN_ID_TYPE,
				textProvider.getText(Texts.userListColumnTitleType), false);
		return Arrays.asList((GridColumn) columnName, (GridColumn) columnType);
	}

	public String getColumnStyle(GridColumn column) {
		return StyleConstants.USER_LIST_COLUMN_PREFIX + column.getId();
	}

	public GridData getData(User user, GridColumn column) {
		if (column.getId().equals(UserList.COLUMN_ID_NAME))
			return new GridData.Text(user.getName());
		else if (column.getId().equals(UserList.COLUMN_ID_TYPE))
			return new GridData.Text(user.getType().getLocalizedText(
					textProvider));
		return new GridData.Text("");
	}

	private static final List<String> ROW_STYLE = Arrays
			.asList(StyleConstants.USER_LIST_ROW);

	public List<String> getRowStyles(User t) {
		return ROW_STYLE;
	}
}*/
