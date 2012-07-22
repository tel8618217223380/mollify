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

import org.sjarvela.mollify.client.filesystem.foldermodel.CurrentFolderProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.dropbox.DropBoxFactory;
import org.sjarvela.mollify.client.ui.formatter.PathFormatter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultDropBoxFactory implements DropBoxFactory {
	private final DragAndDropManager dragAndDropManager;
	private final SessionProvider sessionProvider;
	private final TextProvider textProvider;
	private final PathFormatter pathFormatter;

	@Inject
	public DefaultDropBoxFactory(TextProvider textProvider,
			DragAndDropManager dragAndDropManager,
			SessionProvider sessionProvider, PathFormatter pathFormatter) {
		this.textProvider = textProvider;
		this.dragAndDropManager = dragAndDropManager;
		this.sessionProvider = sessionProvider;
		this.pathFormatter = pathFormatter;
	}

	@Override
	public DropBox createDropBox(
			FileSystemActionHandler fileSystemActionHandler,
			CurrentFolderProvider currentFolderProvider) {
		SessionInfo session = sessionProvider.getSession();
		ActionDelegator actionDelegator = new ActionDelegator();
		DropBoxView view = new DropBoxView(textProvider, actionDelegator,
				session, pathFormatter);
		DropBoxPresenter presenter = new DropBoxPresenter(view, session,
				fileSystemActionHandler, currentFolderProvider);
		return new DropBoxGlue(actionDelegator, view, presenter,
				dragAndDropManager);
	}

}
