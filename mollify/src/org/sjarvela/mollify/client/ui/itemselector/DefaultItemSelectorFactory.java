/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.itemselector;

/*import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DefaultItemSelectorFactory implements ItemSelectorFactory {
	private final TextProvider textProvider;
	private final DialogManager dialogManager;
	private final ViewManager viewManager;

	@Inject
	public DefaultItemSelectorFactory(TextProvider textProvider,
			DialogManager dialogManager, ViewManager viewManager) {
		this.textProvider = textProvider;
		this.dialogManager = dialogManager;
		this.viewManager = viewManager;
	}

	public void openFolderSelector(String title, String message,
			String actionTitle, FileSystemItemProvider provider,
			SelectItemHandler listener, Widget p) {
		SelectItemDialog selectItemDialog = new SelectItemDialog(
				SelectItemDialog.Mode.Folders, dialogManager, textProvider,
				title, message, actionTitle, provider, listener);
		if (p != null)
			viewManager.align(selectItemDialog, p);
	}

	public void openItemSelector(String title, String message,
			String actionTitle, FileSystemItemProvider provider,
			SelectItemHandler listener) {
		new SelectItemDialog(SelectItemDialog.Mode.FoldersAndFiles,
				dialogManager, textProvider, title, message, actionTitle,
				provider, listener);
	}
}*/
