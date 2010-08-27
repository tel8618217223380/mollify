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

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemPermissionHandler;
import org.sjarvela.mollify.client.ui.action.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextPopupListener;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FolderContextGlue implements FolderContextPopup {

	private final FolderContextPresenter presenter;
	private final FileItemContextComponent popup;

	public FolderContextGlue(FileItemContextComponent popup,
			FolderContextPresenter presenter,
			ActionListenerDelegator actionDelegator) {
		this.popup = popup;
		this.presenter = presenter;

		actionDelegator.setActionListener(presenter);
	}

	public void setFolderActionHandler(FileSystemActionHandler actionHandler) {
		presenter.setDirectoryActionHandler(actionHandler);
	}

	public void setFileItemDescriptionHandler(
			FileItemDescriptionHandler descriptionHandler) {
		presenter.setFileItemDescriptionHandler(descriptionHandler);
	}

	public void setFilePermissionHandler(
			FileSystemPermissionHandler permissionHandler) {
		presenter.setPermissionHandler(permissionHandler);
	}

	public void showPopup() {
		popup.showMenu();
	}

	public void hidePopup() {
		popup.hide();
	}

	public void update(Folder folder, Widget parent) {
		popup.setParentWidget(parent);
		presenter.setFolder(folder);
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

}