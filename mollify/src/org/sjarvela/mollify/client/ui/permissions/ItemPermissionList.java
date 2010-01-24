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
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.ui.Formatter;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.grid.DefaultGridColumn;
import org.sjarvela.mollify.client.ui.common.grid.Grid;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.GridDataProvider;

public class ItemPermissionList extends Grid<FileItemUserPermission> implements
		GridDataProvider<FileItemUserPermission> {
	public static GridColumn COLUMN_NAME;
	public static GridColumn COLUMN_PERMISSION;
	public static List<GridColumn> ALL_COLUMNS = null;

	private final TextProvider textProvider;
	private Formatter<FilePermission> filePermissionFormatter;

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
			COLUMN_NAME = new DefaultGridColumn("name", textProvider
					.getStrings().itemPermissionListColumnTitleName(), false);
			COLUMN_PERMISSION = new DefaultGridColumn("permission",
					textProvider.getStrings()
							.itemPermissionListColumnTitlePermission(), false);

			ALL_COLUMNS = Arrays.asList((GridColumn) COLUMN_NAME,
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

		if (column.equals(ItemPermissionList.COLUMN_NAME))
			text = userPermission.getUserOrGroup().getName();
		else if (column.equals(ItemPermissionList.COLUMN_PERMISSION)) {
			FilePermission permission = userPermission.getPermission();

			text = filePermissionFormatter != null ? filePermissionFormatter
					.format(permission) : permission
					.getLocalizedText(textProvider);
		}
		return new GridData.Text(text);
	}

	private static final List<String> ROW_STYLE = Arrays
			.asList(StyleConstants.ITEM_PERMISSION_LIST_ROW);
	private static final List<String> GROUP_STYLE = Arrays
			.asList(StyleConstants.ITEM_PERMISSION_LIST_ROW_GROUP);

	public List<String> getRowStyles(FileItemUserPermission t) {
		if (t.getUserOrGroup().isGroup())
			return GROUP_STYLE;
		return ROW_STYLE;
	}

	public void setPermissionFormatter(
			Formatter<FilePermission> filePermissionFormatter) {
		this.filePermissionFormatter = filePermissionFormatter;
	}

}
