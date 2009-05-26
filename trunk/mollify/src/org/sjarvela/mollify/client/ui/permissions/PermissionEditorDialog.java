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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.user.client.ui.Widget;

public class PermissionEditorDialog extends CenteredDialog {
	private final FileSystemItem item;

	public PermissionEditorDialog(TextProvider textProvider, FileSystemItem item) {
		super(textProvider.getStrings().itemPermissionEditorDialogTitle(),
				StyleConstants.ITEM_PERMISSION_EDITOR_DIALOG);
		this.item = item;
	}

	@Override
	protected Widget createContent() {
		// TODO Auto-generated method stub
		return null;
	}

}
