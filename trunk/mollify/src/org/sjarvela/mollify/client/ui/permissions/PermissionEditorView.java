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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.FilePermissionMode;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.ListBox;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PermissionEditorView extends CenteredDialog {
	private final Label itemName;
	private final ListBox<FilePermissionMode> defaultPermission;
	private final ItemPermissionList list;
	private final TextProvider textProvider;
	private final ActionListener actionListener;

	public enum Actions implements ResourceId {
		close, addPermission, editPermission, removePermission
	}

	public PermissionEditorView(TextProvider textProvider,
			ActionListener actionListener) {
		super(textProvider.getStrings().itemPermissionEditorDialogTitle(),
				StyleConstants.PERMISSION_EDITOR_VIEW);
		this.textProvider = textProvider;
		this.actionListener = actionListener;

		itemName = new Label();
		itemName.setStyleName(StyleConstants.PERMISSION_EDITOR_VIEW_ITEM_NAME);

		list = new ItemPermissionList(textProvider,
				StyleConstants.PERMISSION_EDITOR_VIEW_LIST);

		defaultPermission = new ListBox();
		defaultPermission
				.addStyleName(StyleConstants.PERMISSION_EDITOR_VIEW_DEFAULT_PERMISSION);

		initialize();
	}

	@Override
	protected Widget createContent() {
		Panel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.PERMISSION_EDITOR_VIEW_CONTENT);

		Label itemTitle = new Label(textProvider.getStrings()
				.itemPermissionEditorItemTitle());
		itemTitle
				.setStyleName(StyleConstants.PERMISSION_EDITOR_VIEW_ITEM_TITLE);
		panel.add(itemTitle);
		panel.add(itemName);

		Label defaultPermissionTitle = new Label(textProvider.getStrings()
				.itemPermissionEditorDefaultPermissionTitle());
		defaultPermissionTitle
				.setStyleName(StyleConstants.PERMISSION_EDITOR_VIEW_DEFAULT_PERMISSION_TITLE);
		panel.add(defaultPermissionTitle);
		panel.add(defaultPermission);

		Label listTitle = new Label(textProvider.getStrings()
				.itemPermissionEditorListTitle());
		listTitle
				.setStyleName(StyleConstants.PERMISSION_EDITOR_VIEW_LIST_TITLE);
		panel.add(listTitle);
		panel.add(list);
		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.PERMISSION_EDITOR_VIEW_BUTTONS);
		buttons.add(createButton(textProvider.getStrings().dialogCloseButton(),
				StyleConstants.DIALOG_BUTTON_CLOSE,
				StyleConstants.DIALOG_BUTTON_CLOSE, actionListener,
				Actions.close));

		return buttons;
	}

	public void showProgress(boolean show) {
		if (show)
			itemName.addStyleDependentName(StyleConstants.LOADING);
		else
			itemName.removeStyleDependentName(StyleConstants.LOADING);
	}

	public Label getItemName() {
		return itemName;
	}

	public ListBox<FilePermissionMode> getDefaultPermission() {
		return defaultPermission;
	}

	public ItemPermissionList getList() {
		return list;
	}

}
