/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.directorycontext;

import org.sjarvela.mollify.client.filesystem.provider.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent.Mode;

public class DirectoryContextPopupFactory {
	private final TextProvider textProvider;
	private final DirectoryDetailsProvider detailsProvider;
	private final SessionInfo session;

	public DirectoryContextPopupFactory(TextProvider textProvider,
			DirectoryDetailsProvider detailsProvider, SessionInfo session) {
		this.textProvider = textProvider;
		this.detailsProvider = detailsProvider;
		this.session = session;
	}

	public DirectoryContextPopup createPopup() {
		ActionListenerDelegator actionDelegator = new ActionListenerDelegator();

		boolean descriptionEditable = session.getDefaultPermissionMode()
				.isAdmin()
				&& session.getSettings().isDescriptionUpdateEnabled();

		FileItemContextComponent popup = new FileItemContextComponent(
				Mode.Directory, textProvider, session
						.getDefaultPermissionMode().hasWritePermission(),
				descriptionEditable, session.getSettings()
						.isZipDownloadEnabled(), false, actionDelegator);
		DirectoryContextPresenter presenter = new DirectoryContextPresenter(
				popup, session, detailsProvider, textProvider);
		return new DirectoryContextGlue(popup, presenter, actionDelegator);
	}
}
