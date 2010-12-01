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
import org.sjarvela.mollify.client.localization.Texts;
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
	public static String COLUMN_ID_NAME = "name";
	public static String COLUMN_ID_PERMISSION = "permission";

	private Formatter<FilePermission> filePermissionFormatter;

	public ItemPermissionList(TextProvider textProvider, String style) {
		super(textProvider, StyleConstants.ITEM_PERMISSION_LIST_HEADER);

		this.setStylePrimaryName(StyleConstants.ITEM_PERMISSION_LIST);
		if (style != null)
			this.addStyleDependentName(style);
		this.setDataProvider(this);
	}

	protected List<GridColumn> getColumns() {
		GridColumn columnName = new DefaultGridColumn(COLUMN_ID_NAME,
				textProvider.getText(Texts.itemPermissionListColumnTitleName),
				false);
		GridColumn columnPermission = new DefaultGridColumn(
				COLUMN_ID_PERMISSION,
				textProvider
						.getText(Texts.itemPermissionListColumnTitlePermission),
				false);

		return Arrays.asList((GridColumn) columnName, columnPermission);
	}

	public String getColumnStyle(GridColumn column) {
		return StyleConstants.ITEM_PERMISSION_LIST_COLUMN_PREFIX
				+ column.getId();
	}

	public GridData getData(FileItemUserPermission userPermission,
			GridColumn column) {
		String text = "";

		if (column.getId().equals(ItemPermissionList.COLUMN_ID_NAME))
			text = userPermission.getUserOrGroup().getName();
		else if (column.getId().equals(ItemPermissionList.COLUMN_ID_PERMISSION)) {
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
