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

import org.sjarvela.mollify.client.FileActionProvider;
import org.sjarvela.mollify.client.FileHandler;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.ui.filelist.Column;
import org.sjarvela.mollify.client.ui.filelist.SimpleFileListListener;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class MainViewGlue implements SimpleFileListListener {
	private final MainView view;
	private final MainViewPresenter presenter;

	private FileActionProvider fileActionProvider;
	private FileHandler fileHandler;

	public MainViewGlue(MollifyService service, MainView view,
			MainViewPresenter presenter) {
		this.view = view;
		this.presenter = presenter;

		this.fileHandler = new FileHandlerImpl(service, view, presenter);
		this.fileActionProvider = new FileActionProviderImpl(service, view,
				fileHandler);

		view.addFileListListener(this);
		view.setFileProviders(getFileActionProvider(), presenter);
		view.setFileHandler(fileHandler);

		initializeActions();
	}

	private void initializeActions() {
		view.RefreshButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				presenter.refresh();
			}
		});

		view.ParentDirButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				presenter.moveToParentDirectory();
			}
		});

		view.UploadFileButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				view.openUploadDialog();
			}
		});
	}

	public FileActionProvider getFileActionProvider() {
		return fileActionProvider;
	}

	public FileHandler getFileHandler() {
		return fileHandler;
	}

	public void onDirectoryRowClicked(Directory directory, Column column) {
		if (column.equals(Column.NAME)) {
			presenter.changeDirectory(directory);
		}
	}

	public void onDirectoryUpRowClicked(Column column) {
		presenter.moveToParentDirectory();
	}

	public void onFileRowClicked(File file, Column column) {
		if (column.equals(Column.NAME)) {
			view.showFileActions(file);
		}
	}
}
