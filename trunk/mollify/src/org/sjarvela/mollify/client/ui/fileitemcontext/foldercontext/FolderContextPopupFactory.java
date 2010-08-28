/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.foldercontext;

import org.sjarvela.mollify.client.filesystem.provider.FolderDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.action.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent.Mode;

public class FolderContextPopupFactory {
	private final TextProvider textProvider;
	private final FolderDetailsProvider detailsProvider;
	private final SessionInfo session;
	private final DropBox dropBox;
	private final ItemContextProvider itemContextProvider;
	private final DialogManager dialogManager;

	public FolderContextPopupFactory(DialogManager dialogManager,
			TextProvider textProvider, FolderDetailsProvider detailsProvider,
			SessionInfo session, DropBox dropBox,
			ItemContextProvider itemContextProvider) {
		this.dialogManager = dialogManager;
		this.textProvider = textProvider;
		this.detailsProvider = detailsProvider;
		this.session = session;
		this.dropBox = dropBox;
		this.itemContextProvider = itemContextProvider;
	}

	public FolderContextPopup createPopup() {
		ActionListenerDelegator actionDelegator = new ActionListenerDelegator();

		boolean descriptionEditable = session.getDefaultPermissionMode()
				.isAdmin()
				&& session.getFeatures().descriptionUpdate();
		boolean permissionsEditable = session.getDefaultPermissionMode()
				.isAdmin()
				&& session.getFeatures().permissionUpdate();

		FileItemContextComponent popup = new FileItemContextComponent(
				Mode.Folder, textProvider, session
						.getDefaultPermissionMode().hasWritePermission(),
				descriptionEditable, permissionsEditable, session.getFeatures()
						.zipDownload(), false, false, actionDelegator);
		FolderContextPresenter presenter = new FolderContextPresenter(popup,
				session, detailsProvider, textProvider, dropBox,
				itemContextProvider, dialogManager);
		return new FolderContextGlue(popup, presenter, actionDelegator);
	}
}
