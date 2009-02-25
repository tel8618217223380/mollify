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

import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.request.file.FileDetailsProvider;
import org.sjarvela.mollify.client.session.SessionSettings;

public class FileContextPopupFactory {
	private Localizator localizator;
	private FileDetailsProvider fileDetailsProvider;
	private SessionSettings settings;

	public FileContextPopupFactory(FileDetailsProvider fileDetailsProvider,
			Localizator localizator, SessionSettings settings) {
		this.fileDetailsProvider = fileDetailsProvider;
		this.localizator = localizator;
		this.settings = settings;
	}

	public FileContextPopup createPopup() {
		return new FileContextPopup(localizator, fileDetailsProvider, settings);
	}

}
