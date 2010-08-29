/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.popup;

import org.sjarvela.mollify.client.filesystem.provider.ItemDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.action.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextGlue;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPresenter;

public class ItemContextPopupFactory {
	private final TextProvider textProvider;
	private final ItemDetailsProvider detailsProvider;
	private final SessionInfo session;
	private final DropBox dropBox;
	private final ItemContextProvider itemDetailsProvider;
	private final DialogManager dialogManager;

	public ItemContextPopupFactory(DialogManager dialogManager,
			ItemDetailsProvider detailsProvider, TextProvider textProvider,
			SessionInfo session, DropBox dropBox,
			ItemContextProvider itemDetailsProvider) {
		this.dialogManager = dialogManager;
		this.detailsProvider = detailsProvider;
		this.textProvider = textProvider;
		this.session = session;
		this.dropBox = dropBox;
		this.itemDetailsProvider = itemDetailsProvider;
	}

	public ItemContextPopup createPopup() {
		ActionListenerDelegator actionDelegator = new ActionListenerDelegator();

		ItemContextPopupComponent popup = new ItemContextPopupComponent(
				textProvider, session.getDefaultPermissionMode()
						.hasWritePermission(), actionDelegator);
		ItemContextPresenter presenter = new ItemContextPresenter(popup,
				detailsProvider, textProvider, dropBox, itemDetailsProvider,
				dialogManager);
		return new ItemContextGlue(popup, presenter, actionDelegator);
	}
}
