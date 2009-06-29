/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.permissions;

import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FilePermissionMode;
import org.sjarvela.mollify.client.ui.Formatter;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.Grid;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.GridDataProvider;

public class ItemPermissionList extends Grid<FileItemUserPermission> implements
		GridDataProvider<FileItemUserPermission> {
	public static GridColumn COLUMN_USER;
	public static GridColumn COLUMN_PERMISSION;
	public static List<GridColumn> ALL_COLUMNS = null;

	private final TextProvider textProvider;
	private Formatter<FilePermissionMode> filePermissionFormatter;

	public ItemPermissionList(TextProvider textProvider, String style) {
		super(StyleConstants.ITEM_PERMISSION_LIST_HEADER,
				getColumns(textProvider));
		this.textProvider = textProvider;

		this.setStylePrimaryName(StyleConstants.ITEM_PERMISSION_LIST);
		if (style != null)
			this.addStyleDependentName(style);
		this.setDataProvider(this);
	}

	private static List<GridColumn> getColumns(TextProvider textProvider) {
		if (ALL_COLUMNS == null) {
			COLUMN_USER = new DefaultGridColumn("user", textProvider
					.getStrings().itemPermissionListColumnTitleUser(), false);
			COLUMN_PERMISSION = new DefaultGridColumn("permission",
					textProvider.getStrings()
							.itemPermissionListColumnTitlePermission(), false);

			ALL_COLUMNS = Arrays.asList((GridColumn) COLUMN_USER,
					COLUMN_PERMISSION);
		}

		return ALL_COLUMNS;
	}

	public String getColumnStyle(GridColumn column) {
		return StyleConstants.ITEM_PERMISSION_LIST_COLUMN_PREFIX
				+ column.getId();
	}

	public GridData getData(FileItemUserPermission userPermission,
			GridColumn column) {
		String text = "";

		if (column.equals(ItemPermissionList.COLUMN_USER))
			text = userPermission.getUser().getName();
		else if (column.equals(ItemPermissionList.COLUMN_PERMISSION)) {
			FilePermissionMode permission = userPermission.getPermission();

			text = filePermissionFormatter != null ? filePermissionFormatter
					.format(permission) : permission
					.getLocalizedText(textProvider);
		}
		return new GridData.Text(text);
	}

	private static final List<String> ROW_STYLE = Arrays
			.asList(StyleConstants.ITEM_PERMISSION_LIST_ROW);

	public List<String> getRowStyles(FileItemUserPermission t) {
		return ROW_STYLE;
	}

	public void setPermissionFormatter(
			Formatter<FilePermissionMode> filePermissionFormatter) {
		this.filePermissionFormatter = filePermissionFormatter;
	}

}
