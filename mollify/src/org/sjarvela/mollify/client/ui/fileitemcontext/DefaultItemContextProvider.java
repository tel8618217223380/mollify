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
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.description.DescriptionComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.details.DetailsComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.preview.PreviewComponent;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultItemContextProvider implements ItemContextHandler {
	private final List<ItemContextProvider> providers = new ArrayList();
	private final SessionProvider sessionProvider;
	private final TextProvider textProvider;
	private final ServiceProvider serviceProvider;
	private final DialogManager dialogManager;

	@Inject
	public DefaultItemContextProvider(SessionProvider sessionProvider,
			TextProvider textProvider, ServiceProvider serviceProvider,
			DialogManager dialogManager) {
		this.sessionProvider = sessionProvider;
		this.textProvider = textProvider;
		this.serviceProvider = serviceProvider;
		this.dialogManager = dialogManager;
	}

	@Override
	public ItemContext getItemContext(FileSystemItem item) {
		ItemContext details = createContext(item);
		for (ItemContextProvider provider : providers)
			details = details.add(provider.getItemContext(item));
		return details;
	}

	private ItemContext createContext(FileSystemItem item) {
		List<ItemContextComponent> components = new ArrayList();
		components.add(createDescriptionComponent());
		if (item.isFile()
				&& sessionProvider.getSession().getFeatures().filePreview())
			components.add(createPreviewComponent());
		if (item.isFile())
			components.add(createDetailsComponent());
		return new ItemContext(components);
	}

	private ItemContextComponent createDescriptionComponent() {
		return new DescriptionComponent(textProvider, serviceProvider
				.getFileSystemService(), sessionProvider.getSession(),
				dialogManager);
	}

	private ItemContextComponent createPreviewComponent() {
		return new PreviewComponent(textProvider, serviceProvider
				.getExternalService());
	}

	private ItemContextComponent createDetailsComponent() {
		return new DetailsComponent(textProvider);
	}

	@Override
	public void addItemContextProvider(ItemContextProvider itemContextProvider) {
		providers.add(itemContextProvider);
	}
}
