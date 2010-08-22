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
public class DefaultItemDetailsProvider implements ItemDetailsHandler {
	private final List<ItemDetailsProvider> providers = new ArrayList();

	@Override
	public ItemDetails getItemDetails(FileSystemItem item) {
		ItemDetails details = ItemDetails.Empty;
		for (ItemDetailsProvider provider : providers)
			details = details.merge(provider.getItemDetails(item));
		return details;
	}

	@Override
	public void addItemDetailsProvider(ItemDetailsProvider itemDetailsProvider) {
		providers.add(itemDetailsProvider);
	}

}
