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
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class ItemContextPresenter implements ActionListener,
		ItemContextContainer {
	private final ItemContextPopupComponent popup;
	private final ItemDetailsProvider itemDetailsProvider;
	private final DropBox dropBox;
	private final ItemContextHandler itemContextHandler;
	private final DialogManager dialogManager;

	private FileSystemActionHandler fileSystemActionHandler;

	private FileSystemItem item = null;
	private ItemDetails details;
	private List<ItemContextComponent> components = Collections.EMPTY_LIST;

	public ItemContextPresenter(ItemContextPopupComponent popup,
			ItemDetailsProvider itemDetailsProvider, TextProvider textProvider,
			DropBox dropBox, ItemContextHandler itemContextHandler,
			DialogManager dialogManager) {
		this.popup = popup;
		this.itemDetailsProvider = itemDetailsProvider;
		this.dropBox = dropBox;
		this.itemContextHandler = itemContextHandler;
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

		this.popup.reset();
		this.popup.showProgress();

		popup.getName().setText(item.getName());

		JavaScriptObject data = itemContextHandler
				.getItemContextRequestData(item);

		itemDetailsProvider.getItemDetails(item, data,
				new ResultListener<ItemDetails>() {
					public void onFail(ServiceError error) {
						popup.hide();

						if (error.getDetails() != null
								&& (error.getDetails().startsWith(
										"PHP error #2048") || error
										.getDetails()
										.contains(
												"It is not safe to rely on the system's timezone settings"))) {
							dialogManager
									.showInfo("ERROR",
											"Mollify configuration error, PHP timezone information missing.");
							return;
						}
						dialogManager.showError(error);
					}

					public void onSuccess(ItemDetails details) {
						updateDetails(details);
					}
				});
	}

	private void updateDetails(ItemDetails details) {
		this.popup.hideProgress();

		this.components = new ArrayList();
		if (details != null) {
			components = popup.setup(itemContextHandler.getItemContext(item,
					details), item, details);
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
			popup.hide();
			Object param = null;
			if (action.equals(FileSystemAction.view))
				param = ((FileDetails) details).getFileViewerEditor().getJsObj(
						"view");
			else if (action.equals(FileSystemAction.edit))
				param = ((FileDetails) details).getFileViewerEditor().getJsObj(
						"edit");
			fileSystemActionHandler.onAction(item, (FileSystemAction) action,
					popup, param);
			return;
		}

		if (ItemContextPopupComponent.Action.callback.equals(action)) {
			ContextCallback c = (ContextCallback) o;
			popup.hide();
			c.onContextAction(item);
			return;
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
