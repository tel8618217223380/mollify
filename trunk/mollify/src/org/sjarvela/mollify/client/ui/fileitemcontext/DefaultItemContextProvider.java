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

/*import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.js.JsObjBuilder;
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
import org.sjarvela.mollify.client.ui.fileitemcontext.component.permissions.PermissionsComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.preview.PreviewComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent.Action;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultItemContextProvider implements ItemContextHandler {
	private final List<ItemContextProvider> providers = new ArrayList();
	private final SessionProvider sessionProvider;
	private final TextProvider textProvider;
	private final ServiceProvider serviceProvider;
	private final DialogManager dialogManager;
//	private final PermissionEditorViewFactory permissionEditorViewFactory;

	@Inject
	public DefaultItemContextProvider(SessionProvider sessionProvider,
			TextProvider textProvider, ServiceProvider serviceProvider,
			DialogManager dialogManager) {
		this.sessionProvider = sessionProvider;
		this.textProvider = textProvider;
		this.serviceProvider = serviceProvider;
		this.dialogManager = dialogManager;
//		this.permissionEditorViewFactory = permissionEditorViewFactory;
	}

	@Override
	public JavaScriptObject getItemContextRequestData(JsFilesystemItem item) {
		JsObjBuilder data = new JsObjBuilder();
		for (ItemContextProvider provider : providers)
			data.add(provider.getItemContextRequestData(item));
		return data.create();
	}

	@Override
	public ItemContext getItemContext(JsFilesystemItem item, ItemDetails details) {
		ItemContext context = createContext(item, details);
		for (ItemContextProvider provider : providers)
			context = context.add(provider.getItemContext(item, details), true);
		return context;
	}

	@Override
	public ItemContext getItemActions(JsFilesystemItem item, ItemDetails details) {
		ItemContextBuilder contextBuilder = ItemContext.def();

		boolean writable = isWritable(item, details);
		createSecondaryActions(
				item,
				contextBuilder.actions().type(ItemContext.ActionType.Secondary),
				writable);

		ItemContext context = contextBuilder.create();

		for (ItemContextProvider provider : providers)
			context = context
					.add(provider.getItemContext(item, details), false);

		return context;
	}

	private ItemContext createContext(JsFilesystemItem item, ItemDetails details) {
		ItemContextBuilder context = ItemContext.def();
		createComponents(item, context.components());
		createActions(item, details, context.actions());
		return context.create();
	}

	private void createActions(JsFilesystemItem item, ItemDetails details,
			ItemContextActionsBuilder actions) {
		boolean writable = isWritable(item, details);

		createDownloadActions(item,
				actions.type(ItemContext.ActionType.Download));
		createPrimaryActions(item, details,
				actions.type(ItemContext.ActionType.Primary));
		createSecondaryActions(item,
				actions.type(ItemContext.ActionType.Secondary), writable);
	}

	private boolean isWritable(JsFilesystemItem item, ItemDetails details) {
		if (details == null)
			return false;
		if (!item.isFile() && ((JsFolder) item).isRoot())
			return false;
		return details.getFilePermission().canWrite();
	}

	private void createDownloadActions(JsFilesystemItem item,
			ItemContextActionTypeBuilder actions) {
		if (item.isFile())
			actions.add(FileSystemAction.download,
					textProvider.getText(Texts.fileActionDownloadTitle));
		if (sessionProvider.getSession().getFeatures().zipDownload())
			actions.add(FileSystemAction.download_as_zip,
					textProvider.getText(Texts.fileActionDownloadZippedTitle));
	}

	private void createPrimaryActions(JsFilesystemItem item,
			ItemDetails details, ItemContextActionTypeBuilder actions) {
		if (item.isFile()) {
			FileDetails d = details.cast();
			if (d.getFileViewerEditor() != null
					&& d.getFileViewerEditor().hasValue("view")
					&& sessionProvider.getSession().getFeatures().fileView()) {
				actions.add(FileSystemAction.view,
						textProvider.getText(Texts.fileActionViewTitle));
			}
			if (d.getFileViewerEditor() != null
					&& d.getFileViewerEditor().hasValue("edit")
					&& sessionProvider.getSession().getFeatures().fileEdit()) {
				actions.add(FileSystemAction.edit,
						textProvider.getText(Texts.fileActionEditTitle));
			}
		}
	}

	private void createSecondaryActions(JsFilesystemItem item,
			ItemContextActionTypeBuilder actions, boolean writable) {
		if (item.isFile() || !((JsFolder) item).isRoot())
			actions.add(Action.addToDropbox, textProvider
					.getText(Texts.mainViewSelectActionAddToDropbox));
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

	private void createComponents(JsFilesystemItem item,
			ItemContextComponentsBuilder contextComponents) {
		contextComponents.add(createDescriptionComponent());
		if (item.isFile()
				&& sessionProvider.getSession().getFeatures().filePreview())
			contextComponents.add(createPreviewComponent());
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

	private ItemContextComponent createPermissionsComponent() {
		return new PermissionsComponent(textProvider,
				permissionEditorViewFactory);
	}

	@Override
	public void addItemContextProvider(ItemContextProvider itemContextProvider) {
		providers.add(itemContextProvider);
	}
}*/
