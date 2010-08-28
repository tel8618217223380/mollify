/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.filecontext;

import org.sjarvela.mollify.client.filesystem.provider.FileDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ExternalService;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.action.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent.Mode;

public class FileContextPopupFactory {
	private final TextProvider textProvider;
	private final FileDetailsProvider fileDetailsProvider;
	private final SessionInfo session;
	private final ExternalService service;
	private final DropBox dropBox;
	private final ItemContextProvider itemDetailsProvider;
	private final DialogManager dialogManager;

	public FileContextPopupFactory(DialogManager dialogManager, FileDetailsProvider fileDetailsProvider,
			TextProvider textProvider, SessionInfo session,
			ExternalService service, DropBox dropBox,
			ItemContextProvider itemDetailsProvider) {
		this.dialogManager = dialogManager;
		this.fileDetailsProvider = fileDetailsProvider;
		this.textProvider = textProvider;
		this.session = session;
		this.service = service;
		this.dropBox = dropBox;
		this.itemDetailsProvider = itemDetailsProvider;
	}

	public FileContextPopup createPopup() {
		ActionListenerDelegator actionDelegator = new ActionListenerDelegator();

		boolean permissionsEditable = session.getDefaultPermissionMode()
				.isAdmin()
				&& session.getFeatures().permissionUpdate();

		FileItemContextComponent popup = new FileItemContextComponent(
				Mode.File, textProvider, session.getDefaultPermissionMode()
						.hasWritePermission(), permissionsEditable, session
						.getFeatures().zipDownload(), session.getFeatures()
						.filePreview(), session.getFeatures().fileView(),
				session.getFeatures().publicLinks(), actionDelegator);
		FileContextPresenter presenter = new FileContextPresenter(popup,
				session, fileDetailsProvider, textProvider, service, dropBox,
				itemDetailsProvider, dialogManager);
		return new FileContextGlue(popup, presenter, actionDelegator);
	}
}
