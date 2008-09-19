/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filemanager;

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.FileAction;
import org.sjarvela.mollify.client.FileActionProvider;
import org.sjarvela.mollify.client.FileHandler;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.MollifyService;

import com.google.gwt.user.client.Window;

public class FileActionProviderImpl implements FileActionProvider {
	private final MollifyService service;
	private final MainView view;
	private final FileHandler fileHandler;

	public FileActionProviderImpl(MollifyService service, MainView view,
			FileHandler fileHandler) {
		super();
		this.service = service;
		this.view = view;
		this.fileHandler = fileHandler;
	}

	public String getActionURL(File file, FileAction action) {
		return service.getFileActionUrl(file, action);
	}

	public String getActionURL(Directory dir, FileAction action) {
		return service.getDirectoryActionUrl(dir, action);
	}

	public boolean isActionAllowed(File file, FileAction action) {
		// TODO users rights
		return true;
	}

	public void onFileAction(final File file, FileAction action) {
		if (action.equals(FileAction.DOWNLOAD)) {
			view.openDownloadUrl(this.getActionURL(file, action));
		} else if (action.equals(FileAction.RENAME)) {
			view.showRenameDialog(file);
		} else if (action.equals(FileAction.DELETE)) {
			view.showFileDeleteConfirmationDialog(file,
					new ConfirmationListener() {
						public void onConfirm() {
							fileHandler.onDelete(file);
						}
					});
		} else {
			Window.alert("Unsupported action:" + action.name());
		}
	}

}
