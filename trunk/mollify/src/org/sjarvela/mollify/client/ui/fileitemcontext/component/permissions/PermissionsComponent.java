/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.component.permissions;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionLink;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextContainer;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent.Action;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PermissionsComponent implements ItemContextComponent,
		ActionListener {
	private final TextProvider textProvider;
	private final PermissionEditorViewFactory permissionEditorViewFactory;

	private Widget component;
	private ItemContextContainer container;
	private FileSystemItem item;

	public PermissionsComponent(TextProvider textProvider,
			PermissionEditorViewFactory permissionEditorViewFactory) {
		this.textProvider = textProvider;
		this.permissionEditorViewFactory = permissionEditorViewFactory;
	}

	@Override
	public Widget getComponent() {
		if (component == null)
			component = createContent();
		return component;
	}

	private Widget createContent() {
		ActionLink editPermissions = new ActionLink(
				textProvider.getText(Texts.fileDetailsEditPermissions),
				StyleConstants.FILE_CONTEXT_EDIT_PERMISSIONS,
				StyleConstants.FILE_CONTEXT_PERMISSION_ACTION);
		editPermissions.setAction(this, Action.editPermissions);

		Panel content = new FlowPanel();
		content.setStyleName(StyleConstants.FILE_CONTEXT_PERMISSION_ACTIONS);
		content.add(editPermissions);

		return content;
	}

	@Override
	public boolean onInit(ItemContextContainer container, FileSystemItem item,
			ItemDetails details) {
		this.container = container;
		this.item = item;
		return true;
	}

	@Override
	public void onAction(ResourceId action, Object o) {
		if (ItemContextPopupComponent.Action.editPermissions.equals(action)) {
			container.close();
			permissionEditorViewFactory.openPermissionEditor(item);
		}
	}

	@Override
	public void onContextClose() {
	}
}
