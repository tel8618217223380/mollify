/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.contextpopup.directorycontext;

import org.sjarvela.mollify.client.file.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.file.FileSystemActionHandler;
import org.sjarvela.mollify.client.localization.Localizator;

public class DirectoryContextPopupFactory {
	private final DirectoryDetailsProvider detailsProvider;
	private final FileSystemActionHandler actionHandler;

	public DirectoryContextPopupFactory(Localizator localizator,
			DirectoryDetailsProvider detailsProvider,
			FileSystemActionHandler actionHandler) {
		this.localizator = localizator;
		this.detailsProvider = detailsProvider;
		this.actionHandler = actionHandler;
	}

	private Localizator localizator;

	public DirectoryContextPopup createPopup() {
		return new DirectoryContextPopup(localizator, detailsProvider,
				actionHandler);
	}
}
