/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.provider.ItemDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextCallback;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextContainer;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class ItemContextPresenter implements ActionListener,
		ItemContextContainer {
	private final ItemContextPopupComponent popup;
	private final ItemDetailsProvider itemDetailsProvider;
	private final DropBox dropBox;
	private final ItemContextProvider itemContextProvider;
	private final DialogManager dialogManager;

	private FileSystemActionHandler fileSystemActionHandler;

	private FileSystemItem item = null;
	private ItemDetails details;
	private List<ItemContextComponent> components;

	public ItemContextPresenter(ItemContextPopupComponent popup,
			ItemDetailsProvider itemDetailsProvider, TextProvider textProvider,
			DropBox dropBox, ItemContextProvider itemContextProvider,
			DialogManager dialogManager) {
		this.popup = popup;
		this.itemDetailsProvider = itemDetailsProvider;
		this.dropBox = dropBox;
		this.itemContextProvider = itemContextProvider;
		this.dialogManager = dialogManager;

		popup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				for (ItemContextComponent c : components)
					c.onContextClose();
			}
		});
	}

	public void setFileActionHandler(FileSystemActionHandler actionHandler) {
		this.fileSystemActionHandler = actionHandler;
	}

	public FileSystemItem getItem() {
		return item;
	}

	public void setItem(FileSystemItem item) {
		this.item = item;

		popup.getName().setText(item.getName());
		updateDetails(null);

		itemDetailsProvider.getItemDetails(item,
				new ResultListener<ItemDetails>() {
					public void onFail(ServiceError error) {
						dialogManager.showError(error);
					}

					public void onSuccess(ItemDetails details) {
						updateDetails(details);
					}
				});
	}

	private void updateDetails(ItemDetails details) {
		this.popup.reset();

		this.components = Collections.EMPTY_LIST;
		if (details != null) {
			components = popup.setup(itemContextProvider.getItemContext(item,
					details));
		}

		this.details = details;

		List<ItemContextComponent> rejected = new ArrayList();
		for (ItemContextComponent c : components)
			if (!c.onInit(this, item, details))
				rejected.add(c);
		components.removeAll(rejected);
		popup.removeComponents(rejected);
	}

	public void onAction(ResourceId action, Object o) {
		if (FileSystemAction.class.equals(action.getClass())) {
			Object param = null;
			if (action.equals(FileSystemAction.view))
				param = ((FileDetails) details).getFileView();
			fileSystemActionHandler.onAction(item, (FileSystemAction) action,
					popup, param);
			popup.hide();
			return;
		}

		if (ItemContextPopupComponent.Action.callback.equals(action)) {
			ContextCallback c = (ContextCallback) o;
			c.onContextAction(item);
		} else if (ItemContextPopupComponent.Action.addToDropbox.equals(action)) {
			onAddToDropbox();
		}
	}

	private void onAddToDropbox() {
		dropBox.addItems(Arrays.asList((FileSystemItem) item));
	}

	@Override
	public void close() {
		popup.hide();
	}
}
