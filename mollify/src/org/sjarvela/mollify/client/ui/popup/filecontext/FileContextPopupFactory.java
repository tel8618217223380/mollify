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
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.session.SessionInfo;

public class FileContextPopupFactory {
	private final DefaultTextProvider localizator;
	private final FileDetailsProvider fileDetailsProvider;
	private final SessionInfo sessionInfo;

	public FileContextPopupFactory(FileDetailsProvider fileDetailsProvider,
			DefaultTextProvider localizator, SessionInfo sessionInfo) {
		this.fileDetailsProvider = fileDetailsProvider;
		this.localizator = localizator;
		this.sessionInfo = sessionInfo;
	}

	public FileContextPopup createPopup() {
		return new FileContextPopup(localizator, fileDetailsProvider,
				sessionInfo);
	}

}
