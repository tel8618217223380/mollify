/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.filelist.Column;
import org.sjarvela.mollify.client.ui.filelist.SimpleFileListListener;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class MainViewGlue implements SimpleFileListListener,
		FileUploadListener, ViewListener {
	private final MainView view;
	private final MainViewPresenter presenter;
	private final FileUploadHandler fileUploadHandler;
	private final WindowManager windowManager;
	private final FileViewModel model;
	private final FileActionProvider fileActionProvider;

	public MainViewGlue(WindowManager windowManager, FileViewModel model,
			MainView view, final MainViewPresenter presenter,
			FileActionProvider fileActionProvider,
			FileActionHandler fileActionHandler,
			FileUploadHandler fileUploadHandler) {
		this.windowManager = windowManager;
		this.model = model;
		this.view = view;
		this.presenter = presenter;
		this.fileActionProvider = fileActionProvider;

		this.fileUploadHandler = fileUploadHandler;
		this.fileUploadHandler.addListener(this);

		view.addFileListListener(this);
		view.addViewListener(this);

		fileActionHandler.addRenameListener(new ResultListener() {

			public void onFail(ServiceError error) {
				presenter.onError(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				presenter.refresh();
			}
		});

		fileActionHandler.addDeleteListener(new ResultListener() {

			public void onFail(ServiceError error) {
				presenter.onError(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				presenter.refresh();
			}
		});

		initializeActions();
	}

	public void onViewLoad() {
		presenter.onRefreshRootDirectories();
	}

	private void initializeActions() {
		view.getRefreshButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				presenter.refresh();
			}
		});

		view.getParentDirButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				presenter.moveToParentDirectory();
			}
		});

		view.getUploadFileButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				if (!presenter.isUploadAllowed())
					return;

				windowManager.getDialogManager().openUploadDialog(
						model.getDirectoryModel().getCurrentFolder(),
						fileActionProvider, fileUploadHandler);
			}
		});
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
			view.showFileDetails(file);
		}
	}

	public void onUploadStarted() {

	}

	public void onUploadFinished() {
		presenter.refresh();
	}

	public void onUploadFailed(ServiceError error) {
		presenter.onError(error);
	}

}
