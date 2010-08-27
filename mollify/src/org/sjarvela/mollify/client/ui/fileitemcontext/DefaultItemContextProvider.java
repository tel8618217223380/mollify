/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;

import com.google.inject.Singleton;

@Singleton
public class DefaultItemContextProvider implements ItemContextHandler {
	private final List<ItemContextProvider> providers = new ArrayList();

	@Override
	public ItemContext getItemContext(FileSystemItem item) {
		ItemContext details = ItemContext.Empty;
		for (ItemContextProvider provider : providers)
			details = details.merge(provider.getItemContext(item));
		return details;
	}

	@Override
	public void addItemDetailsProvider(ItemContextProvider itemDetailsProvider) {
		providers.add(itemDetailsProvider);
	}

}