/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.popup.filecontext;

import org.sjarvela.mollify.client.filesystem.provider.FileDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.ActionListenerDelegator;

public class FileContextPopupFactory {
	private final TextProvider textProvider;
	private final FileDetailsProvider fileDetailsProvider;
	private final SessionInfo session;

	public FileContextPopupFactory(FileDetailsProvider fileDetailsProvider,
			TextProvider textProvider, SessionInfo session) {
		this.fileDetailsProvider = fileDetailsProvider;
		this.textProvider = textProvider;
		this.session = session;
	}

	public FileContextPopup createPopup() {
		ActionListenerDelegator actionDelegator = new ActionListenerDelegator();
		FileContextPopupComponent popup = new FileContextPopupComponent(
				textProvider, session, actionDelegator);
		FileContextPresenter presenter = new FileContextPresenter(popup,
				session, fileDetailsProvider, textProvider);
		return new FileContextGlue(popup, presenter, actionDelegator);
	}

}
