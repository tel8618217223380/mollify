/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.foldercontext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderDetails;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemPermissionHandler;
import org.sjarvela.mollify.client.filesystem.provider.FolderDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent;

public class FolderContextPresenter implements ActionListener {
	private final ItemContextPopupComponent popup;
	private final FolderDetailsProvider detailsProvider;
	private final DropBox dropBox;
	private final ItemContextProvider itemContextProvider;
	private final DialogManager dialogManager;

	private FileSystemActionHandler fileSystemActionHandler;
	private FileSystemPermissionHandler permissionHandler;

	private Folder folder;
	private List<ItemContextComponent> components;

	public FolderContextPresenter(ItemContextPopupComponent popup,
			SessionInfo session, FolderDetailsProvider detailsProvider,
			TextProvider textProvider, DropBox dropBox,
			ItemContextProvider itemContextProvider, DialogManager dialogManager) {
		this.popup = popup;
		this.detailsProvider = detailsProvider;
		this.dropBox = dropBox;
		this.itemContextProvider = itemContextProvider;
		this.dialogManager = dialogManager;
	}

	public void setDirectoryActionHandler(FileSystemActionHandler actionHandler) {
		this.fileSystemActionHandler = actionHandler;
	}

	public void setPermissionHandler(
			FileSystemPermissionHandler permissionHandler) {
		this.permissionHandler = permissionHandler;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;

		popup.getName().setText(folder.getName());
		updateDetails(null);

		detailsProvider.getFolderDetails(folder,
				new ResultListener<FolderDetails>() {
					public void onFail(ServiceError error) {
						dialogManager.showError(error);
					}

					public void onSuccess(FolderDetails details) {
						updateDetails(details);
					}
				});
	}

	protected void updateDetails(FolderDetails details) {
		this.popup.reset();

		this.components = Collections.EMPTY_LIST;
		if (details != null) {
			components = popup.createComponents(itemContextProvider
					.getItemContext(folder));
		}

		boolean writable = (details == null ? false : details
				.getFilePermission().canWrite());

		this.popup.update(writable, false);

		List<ItemContextComponent> rejected = new ArrayList();
		for (ItemContextComponent c : components)
			if (!c.onInit(folder, details))
				rejected.add(c);
		components.removeAll(rejected);
		popup.removeComponents(rejected);
	}

	public void onAction(ResourceId action, Object o) {
		if (FileSystemAction.class.equals(action.getClass())) {
			fileSystemActionHandler.onAction(folder, (FileSystemAction) action,
					popup, null);
			popup.hide();
			return;
		}

		if (ItemContextPopupComponent.Action.addToDropbox.equals(action))
			onAddToDropbox();
		else if (ItemContextPopupComponent.Action.editPermissions
				.equals(action)) {
			popup.hide();
			permissionHandler.onEditPermissions(folder);
		}
	}

	private void onAddToDropbox() {
		dropBox.addItems(Arrays.asList((FileSystemItem) folder));
	}

}
