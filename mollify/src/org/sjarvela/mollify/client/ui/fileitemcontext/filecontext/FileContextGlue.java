/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.filecontext;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.ui.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextPopupListener;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileContextGlue implements FileContextPopup {

	private final FileContextPresenter presenter;
	private final FileItemContextComponent popup;

	public FileContextGlue(FileItemContextComponent popup,
			FileContextPresenter presenter,
			ActionListenerDelegator actionDelegator) {
		this.popup = popup;
		this.presenter = presenter;

		actionDelegator.setActionListener(presenter);
	}

	public void setFileActionHandler(FileSystemActionHandler actionHandler) {
		presenter.setFileActionHandler(actionHandler);
	}

	public void setFileItemDescriptionHandler(
			FileItemDescriptionHandler descriptionHandler) {
		presenter.setFileItemDescriptionHandler(descriptionHandler);
	}

	public void showPopup() {
		popup.showMenu();
	}

	public void update(File file, Widget parent) {
		popup.setParentWidget(parent);
		presenter.setFile(file);
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
