/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.directorycontext;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.ui.ActionListenerDelegator;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;

import com.google.gwt.user.client.Element;

public class DirectoryContextGlue implements DirectoryContextPopup {

	private final DirectoryContextPresenter presenter;
	private final FileItemContextComponent popup;

	public DirectoryContextGlue(FileItemContextComponent popup,
			DirectoryContextPresenter presenter,
			ActionListenerDelegator actionDelegator) {
		this.popup = popup;
		this.presenter = presenter;

		actionDelegator.setActionListener(presenter);
	}

	public void setDirectoryActionHandler(FileSystemActionHandler actionHandler) {
		presenter.setDirectoryActionHandler(actionHandler);
	}

	public void setFileItemDescriptionHandler(
			FileItemDescriptionHandler descriptionHandler) {
		presenter.setFileItemDescriptionHandler(descriptionHandler);
	}

	public void showMenu() {
		popup.showMenu();
	}

	public void update(Directory directory, Element parent) {
		popup.setParent(parent);
		presenter.setDirectory(directory);
	}

}
