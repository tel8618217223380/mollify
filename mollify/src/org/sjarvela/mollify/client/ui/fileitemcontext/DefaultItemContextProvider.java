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

import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.description.DescriptionComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.details.DetailsComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.permissions.PermissionsComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.preview.PreviewComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent.Action;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultItemContextProvider implements ItemContextHandler {
	private final List<ItemContextProvider> providers = new ArrayList();
	private final SessionProvider sessionProvider;
	private final TextProvider textProvider;
	private final ServiceProvider serviceProvider;
	private final DialogManager dialogManager;
	private final PermissionEditorViewFactory permissionEditorViewFactory;

	@Inject
	public DefaultItemContextProvider(SessionProvider sessionProvider,
			TextProvider textProvider, ServiceProvider serviceProvider,
			DialogManager dialogManager,
			PermissionEditorViewFactory permissionEditorViewFactory) {
		this.sessionProvider = sessionProvider;
		this.textProvider = textProvider;
		this.serviceProvider = serviceProvider;
		this.dialogManager = dialogManager;
		this.permissionEditorViewFactory = permissionEditorViewFactory;
	}

	@Override
	public ItemContext getItemContext(FileSystemItem item, ItemDetails details) {
		ItemContext context = createContext(item, details);
		for (ItemContextProvider provider : providers)
			context = context.add(provider.getItemContext(item, details));
		return context;
	}

	private ItemContext createContext(FileSystemItem item, ItemDetails details) {
		List<ItemContextComponent> components = getComponents(item);
		List<MenuItem> downloadItems = getDownloadItems(item);
		List<MenuItem> actionItems = getActionItems(item, details);
		return new ItemContext(components, downloadItems, actionItems);
	}

	private List<MenuItem> getDownloadItems(FileSystemItem item) {
		List<MenuItem> items = new ArrayList();
		if (item.isFile())
			items.add(new ActionMenuItem(FileSystemAction.download,
					textProvider.getStrings().fileActionDownloadTitle()));
		if (sessionProvider.getSession().getFeatures().zipDownload())
			items.add(new ActionMenuItem(FileSystemAction.download_as_zip,
					textProvider.getStrings().fileActionDownloadZippedTitle()));

		return items;
	}

	private List<MenuItem> getActionItems(FileSystemItem item,
			ItemDetails details) {
		boolean writable = (details == null ? false : details
				.getFilePermission().canWrite());

		List<MenuItem> actions = new ArrayList();

		actions.add(new ActionMenuItem(Action.addToDropbox, textProvider
				.getStrings().mainViewSelectActionAddToDropbox()));
		if (item.isFile()
				&& sessionProvider.getSession().getFeatures().publicLinks())
			actions.add(new ActionMenuItem(FileSystemAction.publicLink,
					textProvider.getStrings().fileActionPublicLinkTitle()));
		actions.add(new MenuSeparator());
		if (writable)
			actions.add(new ActionMenuItem(FileSystemAction.rename,
					textProvider.getStrings().fileActionRenameTitle()));
		actions.add(new ActionMenuItem(FileSystemAction.copy, textProvider
				.getStrings().fileActionCopyTitle()));
		if (item.isFile())
			actions.add(new ActionMenuItem(FileSystemAction.copyHere,
					textProvider.getStrings().fileActionCopyHereTitle()));
		if (writable)
			actions.add(new ActionMenuItem(FileSystemAction.move, textProvider
					.getStrings().fileActionMoveTitle()));
		if (writable)
			actions.add(new ActionMenuItem(FileSystemAction.delete,
					textProvider.getStrings().fileActionDeleteTitle()));
		return actions;
	}

	private List<ItemContextComponent> getComponents(FileSystemItem item) {
		List<ItemContextComponent> components = new ArrayList();
		components.add(createDescriptionComponent());
		if (item.isFile()
				&& sessionProvider.getSession().getFeatures().filePreview())
			components.add(createPreviewComponent());
		if (item.isFile())
			components.add(createDetailsComponent());
		if (sessionProvider.getSession().getDefaultPermissionMode().isAdmin())
			components.add(createPermissionsComponent());
		return components;
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

	private ItemContextComponent createPermissionsComponent() {
		return new PermissionsComponent(textProvider,
				permissionEditorViewFactory);
	}

	@Override
	public void addItemContextProvider(ItemContextProvider itemContextProvider) {
		providers.add(itemContextProvider);
	}
}
