/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.popup.directorycontext;

import org.sjarvela.mollify.client.filesystem.provider.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.SessionSettings;

public class DirectoryContextPopupFactory {
	private final TextProvider textProvider;
	private final DirectoryDetailsProvider detailsProvider;
	private final SessionSettings settings;

	public DirectoryContextPopupFactory(TextProvider textProvider,
			DirectoryDetailsProvider detailsProvider, SessionSettings settings) {
		this.textProvider = textProvider;
		this.detailsProvider = detailsProvider;
		this.settings = settings;
	}

	public DirectoryContextPopup createPopup() {
		return new DirectoryContextPopup(textProvider, detailsProvider, settings);
	}
}
