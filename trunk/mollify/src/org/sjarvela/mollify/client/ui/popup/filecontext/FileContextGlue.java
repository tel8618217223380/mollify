/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.popup.filecontext;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.ui.ActionListenerDelegator;

import com.google.gwt.user.client.Element;

public class FileContextGlue implements FileContextPopup {

	private final FileContextPresenter presenter;
	private final FileContextPopupComponent popup;

	public FileContextGlue(FileContextPopupComponent popup,
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

	public void showMenu() {
		popup.showMenu();
	}

	public void update(File file, Element parent) {
		popup.setParent(parent);
		presenter.setFile(file);
	}

}
