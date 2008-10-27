/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileaction;

import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileDetailsProvider;
import org.sjarvela.mollify.client.localization.Localizator;

public class FileDetailsPopupFactory {
	public FileDetailsPopupFactory(FileActionHandler fileActionHandler,
			FileDetailsProvider fileDetailsProvider, Localizator localizator) {
		this.fileActionHandler = fileActionHandler;
		this.fileDetailsProvider = fileDetailsProvider;
		this.localizator = localizator;
	}

	private Localizator localizator;
	private FileDetailsProvider fileDetailsProvider;
	private FileActionHandler fileActionHandler;

	public FileDetailsPopup createPopup() {
		return new FileDetailsPopup(localizator, fileDetailsProvider,
				fileActionHandler);
	}

}
