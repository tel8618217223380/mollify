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

import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext.ItemContextActionTypeBuilder;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext.ItemContextActionsBuilder;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext.ItemContextBuilder;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext.ItemContextComponentsBuilder;
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
		ItemContextBuilder context = ItemContext.def();
		createComponents(item, context.components());
		createActions(item, details, context.actions());
		return context.create();
	}

	private void createActions(FileSystemItem item, ItemDetails details,
			ItemContextActionsBuilder actions) {
		boolean writable = isWritable(item, details);

		createDownloadActions(item,
				actions.type(ItemContext.ActionType.Download));
		createPrimaryActions(item, details,
				actions.type(ItemContext.ActionType.Primary));
		createSecondaryActions(item,
				actions.type(ItemContext.ActionType.Secondary), writable);
	}

	private boolean isWritable(FileSystemItem item, ItemDetails details) {
		if (details == null)
			return false;
		if (!item.isFile() && ((Folder) item).isRoot())
			return false;
		return details.getFilePermission().canWrite();
	}

	private void createDownloadActions(FileSystemItem item,
			ItemContextActionTypeBuilder actions) {
		if (item.isFile())
			actions.add(FileSystemAction.download,
					textProvider.getText(Texts.fileActionDownloadTitle));
		if (sessionProvider.getSession().getFeatures().zipDownload())
			actions.add(FileSystemAction.download_as_zip,
					textProvider.getText(Texts.fileActionDownloadZippedTitle));
	}

	private void createPrimaryActions(FileSystemItem item, ItemDetails details,
			ItemContextActionTypeBuilder actions) {
		if (item.isFile()) {
			FileDetails d = details.cast();
			if (d.getFileView() != null
					&& sessionProvider.getSession().getFeatures().fileView()) {
				actions.add(FileSystemAction.view,
						textProvider.getText(Texts.fileActionViewTitle));
			}
		}
	}

	private void createSecondaryActions(FileSystemItem item,
			ItemContextActionTypeBuilder actions, boolean writable) {
		if (item.isFile() || !((Folder) item).isRoot())
			actions.add(Action.addToDropbox, textProvider
					.getText(Texts.mainViewSelectActionAddToDropbox));
		if (item.isFile()
				&& sessionProvider.getSession().getFeatures().publicLinks())
			actions.add(FileSystemAction.publicLink,
					textProvider.getText(Texts.fileActionPublicLinkTitle));
		actions.addSeparator();
		if (writable)
			actions.add(FileSystemAction.rename,
					textProvider.getText(Texts.fileActionRenameTitle));
		actions.add(FileSystemAction.copy,
				textProvider.getText(Texts.fileActionCopyTitle));
		if (item.isFile())
			actions.add(FileSystemAction.copyHere,
					textProvider.getText(Texts.fileActionCopyHereTitle));
		if (writable)
			actions.add(FileSystemAction.move,
					textProvider.getText(Texts.fileActionMoveTitle));
		if (writable)
			actions.add(FileSystemAction.delete,
					textProvider.getText(Texts.fileActionDeleteTitle));
	}

	private void createComponents(FileSystemItem item,
			ItemContextComponentsBuilder contextComponents) {
		contextComponents.add(createDescriptionComponent());
		if (item.isFile()
				&& sessionProvider.getSession().getFeatures().filePreview())
			contextComponents.add(createPreviewComponent());
		if (item.isFile())
			contextComponents.add(createDetailsComponent());
		if (sessionProvider.getSession().getDefaultPermissionMode().isAdmin())
			contextComponents.add(createPermissionsComponent());
	}

	private ItemContextComponent createDescriptionComponent() {
		return new DescriptionComponent(textProvider,
				serviceProvider.getFileSystemService(),
				sessionProvider.getSession(), dialogManager);
	}

	private ItemContextComponent createPreviewComponent() {
		return new PreviewComponent(textProvider,
				serviceProvider.getExternalService());
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
