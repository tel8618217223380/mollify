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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.ui.action.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ContextPopupListener;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ItemContextPopup;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class ItemContextGlue implements ItemContextPopup {

	private final ItemContextPresenter presenter;
	private final ItemContextPopupComponent popup;

	public ItemContextGlue(ItemContextPopupComponent popup,
			ItemContextPresenter presenter,
			ActionListenerDelegator actionDelegator) {
		this.popup = popup;
		this.presenter = presenter;

		actionDelegator.setActionListener(presenter);
	}

	public void setActionHandler(FileSystemActionHandler actionHandler) {
		presenter.setFileActionHandler(actionHandler);
	}

	public void showPopup() {
		popup.showPopup();
	}

	public void hidePopup() {
		popup.hide();
	}

	public void update(FileSystemItem item, Element parent) {
		popup.setParentElement(parent);
		presenter.setItem(item);
	}

	public void addPopupListener(final ContextPopupListener contextPopupListener) {
		popup.addCloseHandler(new CloseHandler<PopupPanel>() {
			public void onClose(CloseEvent<PopupPanel> event) {
				contextPopupListener.onPopupClosed();
			}
		});
	}

	public void setPopupPositioner(PopupPositioner positioner) {
		popup.setPositioner(positioner);
	}

	@Override
	public void showMenu(FileSystemItem t, Element parent) {
		presenter.showMenu(t, parent);
	}

}
