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
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PermissionEditorView extends CenteredDialog {
	private final Label fileName;
	private final ItemPermissionList list;
	private final TextProvider textProvider;
	private final ActionListener actionListener;

	public enum Actions implements ResourceId {
		close
	}

	public PermissionEditorView(TextProvider textProvider,
			ActionListener actionListener) {
		super(textProvider.getStrings().itemPermissionEditorDialogTitle(),
				StyleConstants.ITEM_PERMISSION_EDITOR_DIALOG);
		this.textProvider = textProvider;
		this.actionListener = actionListener;

		fileName = new Label();
		list = new ItemPermissionList(textProvider,
				StyleConstants.PERMISSION_EDITOR_VIEW_LIST);
	}

	@Override
	protected Widget createContent() {
		Panel panel = new HorizontalPanel();
		panel.add(fileName);
		panel.add(list);
		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.RENAME_DIALOG_BUTTONS);
		buttons.add(createButton(textProvider.getStrings().dialogCloseButton(),
				StyleConstants.DIALOG_BUTTON_CLOSE,
				StyleConstants.DIALOG_BUTTON_CLOSE, actionListener,
				Actions.close));

		return buttons;
	}

	public ItemPermissionList getList() {
		return list;
	}

}
