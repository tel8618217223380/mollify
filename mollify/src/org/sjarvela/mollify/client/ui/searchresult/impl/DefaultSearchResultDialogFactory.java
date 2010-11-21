/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.searchresult.impl;

import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.searchresult.SearchResultDialogFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultSearchResultDialogFactory implements
		SearchResultDialogFactory {
	private final TextProvider textProvider;

	@Inject
	public DefaultSearchResultDialogFactory(TextProvider textProvider) {
		this.textProvider = textProvider;
	}

	@Override
	public void show(String criteria, SearchResult result) {
		new SearchResultDialog(textProvider, criteria, result).center();
	}

}
