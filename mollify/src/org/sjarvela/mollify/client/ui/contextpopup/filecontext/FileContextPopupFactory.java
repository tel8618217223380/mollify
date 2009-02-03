/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.contextpopup.filecontext;

import org.sjarvela.mollify.client.data.SessionSettings;
import org.sjarvela.mollify.client.file.FileSystemActionHandler;
import org.sjarvela.mollify.client.file.FileDetailsProvider;
import org.sjarvela.mollify.client.localization.Localizator;

public class FileContextPopupFactory {
	private Localizator localizator;
	private FileDetailsProvider fileDetailsProvider;
	private FileSystemActionHandler fileActionHandler;
	private SessionSettings settings;

	public FileContextPopupFactory(FileSystemActionHandler fileActionHandler,
			FileDetailsProvider fileDetailsProvider, Localizator localizator, SessionSettings settings) {
		this.fileActionHandler = fileActionHandler;
		this.fileDetailsProvider = fileDetailsProvider;
		this.localizator = localizator;
		this.settings = settings;
	}
	
	public FileContextPopup createPopup() {
		return new FileContextPopup(localizator, fileDetailsProvider,
				fileActionHandler, settings);
	}

}
