/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dropbox.impl;

import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.foldermodel.CurrentFolderProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.dropbox.DropBoxFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultDropBoxFactory implements DropBoxFactory {
	private final DragAndDropManager dragAndDropManager;
	private final SessionProvider sessionProvider;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final TextProvider textProvider;

	@Inject
	public DefaultDropBoxFactory(TextProvider textProvider, DragAndDropManager dragAndDropManager,
			SessionProvider sessionProvider,
			FileSystemItemProvider fileSystemItemProvider) {
		this.textProvider = textProvider;
		this.dragAndDropManager = dragAndDropManager;
		this.sessionProvider = sessionProvider;
		this.fileSystemItemProvider = fileSystemItemProvider;
	}

	@Override
	public DropBox createDropBox(
			FileSystemActionHandler fileSystemActionHandler,
			CurrentFolderProvider currentFolderProvider) {
		SessionInfo session = sessionProvider.getSession();
		ActionDelegator actionDelegator = new ActionDelegator();
		DropBoxView view = new DropBoxView(textProvider, actionDelegator,
				fileSystemItemProvider, session);
		DropBoxPresenter presenter = new DropBoxPresenter(view, session,
				fileSystemActionHandler, currentFolderProvider);
		return new DropBoxGlue(actionDelegator, view, presenter,
				dragAndDropManager);
	}

}
