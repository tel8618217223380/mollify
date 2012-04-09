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

/*import org.sjarvela.mollify.client.filesystem.provider.ItemDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.action.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextGlue;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPresenter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultItemContextPopupFactory implements ItemContextPopupFactory {
	private final TextProvider textProvider;
	private final ItemDetailsProvider detailsProvider;
	private final ItemContextHandler itemContextHandler;
	private final DialogManager dialogManager;
	private final SessionProvider sessionProvider;

	@Inject
	public DefaultItemContextPopupFactory(DialogManager dialogManager,
			ItemDetailsProvider detailsProvider, TextProvider textProvider,
			SessionProvider sessionProvider,
			ItemContextHandler itemContextHandler) {
		this.dialogManager = dialogManager;
		this.detailsProvider = detailsProvider;
		this.textProvider = textProvider;
		this.sessionProvider = sessionProvider;
		this.itemContextHandler = itemContextHandler;
	}

	public ItemContextPopup createPopup(DropBox dropBox) {
		ActionListenerDelegator actionDelegator = new ActionListenerDelegator();

		ItemContextPopupComponent popup = new ItemContextPopupComponent(
				textProvider, sessionProvider.getSession()
						.getDefaultPermissionMode().hasWritePermission(),
				actionDelegator);
		ItemContextPresenter presenter = new ItemContextPresenter(popup,
				detailsProvider, textProvider, dropBox, itemContextHandler,
				dialogManager);
		return new ItemContextGlue(popup, presenter, actionDelegator);
	}
}*/
